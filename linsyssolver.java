import java.util.Scanner;
import java.util.ArrayList;

/**
 * Solves linear systems.
 * @author Nolan Bridges
 * @version 1.0.0
 */
public class linsyssolver {
    public static void main( String[] args ) {
        /** The Scanner object that detects user input. */
        Scanner scan = new Scanner(System.in);

        /** Instructions */
        System.out.println("\n      ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~"
                         + "\n     ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~"
                         + "\n    ~ ~ ~ ~ ~ ~ ~ ~                             ~ ~ ~ ~ ~ ~ ~ ~"
                         + "\n   ~ ~ ~ ~ ~ ~ ~ ~     LINEAR SYSTEM SOLVER      ~ ~ ~ ~ ~ ~ ~ ~"
                         + "\n    ~ ~ ~ ~ ~ ~ ~ ~                             ~ ~ ~ ~ ~ ~ ~ ~" 
                         + "\n     ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~"
                         + "\n      ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~\n\n"
                         + "   ~~~~~~~~~~~~~~~~~~~     Instructions    ~~~~~~~~~~~~~~~~~~~\n\n"
                         + "   - Type in the coefficients separated by spaces.\n"
                         + "   - Coefficients of 0 should be put as 0.\n"
                         + "   - The last value should be the constant that the\n"
                         + "     equation is equal to.\n"
                         + "   - Return a blank line when done inputting equations.\n\n"
                         + "   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        /** The number of equations the user inputs. */
        int count = 1;
        /** Whether or not the user wants to keep adding equations. */
        boolean more = true;
        /**
         *  The number of variables per equation. Default is -1.
         *  It must be the same between equations.
         */
        int variableCount = -1;

        /** ArrayList of LinEq objects that stores the coefficients/constant of each equation */
        ArrayList<LinEq> lineqs = new ArrayList<>();

        /**
         * While loop that keeps going while user adds more equations.
         */
        while (more) {
            /** Prints prompt for user to input an equation. */
            System.out.println("Equation " + count + ":");

            /** Retrieves the user inputted line. */
            String userInput = scan.nextLine();
            
            /**
             *  If the line is blank, the while loop ends and the
             *  program tries to solve the system. Otherwise, it
             *  parses the data.
             */
            if (userInput.trim().equals("")) {
                more = false;
            } else {
                /** Splits the user input into coefficients/constant. */
                String[] coefficients = userInput.trim().split(" ");
                
                /**
                 *  Sets the number of coefficients/constant per line if not
                 *  done already. Otherwise checks if there is the right number
                 *  of coefficients/constant.
                 */
                if (variableCount == -1) {
                    variableCount = coefficients.length;
                } else if (coefficients.length != variableCount) {
                    /**
                     *  If there is not the right number of coefficients/constant,
                     *  it will reject the user input and retry next loop.
                     */
                    count -= 1;
                    System.out.println("Invalid equation. Expected " + variableCount
                        + " values but received " + coefficients.length + ".");
                    continue;
                }

                /** Transforms the coefficients/constant from String to double form. */
                double[] doubleCoeffs = new double[variableCount];
                for (int index = 0; index < variableCount; index++) {
                    doubleCoeffs[index] = Double.parseDouble(coefficients[index]);
                }

                /** 
                 * Creates a new LinEq object and adds it to the ArrayList of LinEqs
                 * that represent the coefficients/constant of the user's equations.
                 */
                lineqs.add(new LinEq(doubleCoeffs));
            }
            /** Increases the number of equations by 1. */
            count += 1;
        }

        System.out.println("  Original system:");
        printSystem(lineqs);

        /** Attempts to solve the linear system. */

        /**
         * Goes along the main diagonal of the linear system's 'matrix'
         * and pivots the lines below. Gets matrix in row echelon form.
         */
        for (int column = 0; column < variableCount; column++) {
            /** Used to prevent infinite loops. */
            int overloadCounter = 0;
            for (int row = column; row < lineqs.size(); row++) {
                /** Checks if current entry is not along main diagonal. */
                if (row != column) {
                    /**
                     * Zeroes the current entry by adding a multiple of the row
                     * of the current column's pivot to the current entry's row.
                     */
                    LinEq tempLinEq = new LinEq(lineqs.get(column));
                    if (tempLinEq.getVals()[column] != 0.0) {
                        tempLinEq.multiply(-1.0 * lineqs.get(row).getVals()[column] / tempLinEq.getVals()[column]);
                        lineqs.get(row).add(tempLinEq);
                    }
                } else {
                    /** Checks if the value along the diagonal is not 0. */
                    if (lineqs.get(row).getVals()[column] != 0.0) {
                        /** Scales row such that the value along the main diagonal is 1.0. */
                        lineqs.get(row).multiply(1.0 / lineqs.get(row).getVals()[column]);
                    } else {
                        /** Since the value along diagonal is 0, it will move the equation to the bottom. */
                        lineqs.add(lineqs.get(row));
                        lineqs.remove(row);

                        /** Goes back up a row after shifting this row down. */
                        row -= 1;
                        /** Used to prevent infinite loops should rows keep cycling to the bottom. */
                        overloadCounter += 1;
                        if (overloadCounter > lineqs.size() - 1) {
                            break;
                        }
                    }
                }
            }
        }
        
        System.out.println("\n  Row echelon form:");
        printSystem(lineqs);

        /**
         * Pivots from the bottom up. Gets matrix in reduced row echelon form.
         */
        for (int row = lineqs.size() - 1; row > 0; row--) {
            /**
             * Finds first non-zero, non-constant term in the row.
             */
            int firstIndex = -1;
            for (int column = row; column < variableCount - 1; column++) {
                if (firstIndex == -1 && lineqs.get(row).getVals()[column] != 0.0) {
                    firstIndex = column;
                }
            }
            
            /**
             * Zeroes the elements in the rows above it by pivoting around the
             * first non-zero index.
             */
            if (firstIndex != -1) {
                for (int rowToZero = row - 1; rowToZero >= 0; rowToZero--) {
                    LinEq tempLinEq = new LinEq(lineqs.get(row));
                    if (tempLinEq.getVals()[firstIndex] != 0.0) {
                        tempLinEq.multiply(-1.0 * lineqs.get(rowToZero).getVals()[firstIndex] / tempLinEq.getVals()[firstIndex]);
                        lineqs.get(rowToZero).add(tempLinEq);
                    }
                }
            }
        }

        System.out.println("\n  Reduced row echelon form:");
        printSystem(lineqs);

        /** Prints the results in a readable form. */
        String out = "\n   ~~~~~~~~~~~~~~~~~~~~~~    Results    ~~~~~~~~~~~~~~~~~~~~~~   \n\n\t";
        for (int row = 0; row < lineqs.size(); row++) {
            /** The output string to build upon for the row. */
            String rowOut = "";
            /** The index of the leading term. */
            int firstIndex = -1;
            /**
             * Whether or not the linear equation has non-zero coefficients
             * after the leading variable.
             */
            boolean hasVariables = false;
            /** Whether or not the constant that the linear equation equals is 0.0. */
            boolean hasNonZeroConstant = false;
            
            /** Parses through the row to determine what non-zero terms it has. */
            for (int column = row; column < variableCount; column++) {
                if (firstIndex == -1 && lineqs.get(row).getVals()[column] != 0.0) {
                    if (column == variableCount - 1) {
                        /** If the first non-zero term is the constant, the system has no solution. */
                        System.out.println("\n   ~~~~~~~~~~~~~~~~~~~~~~    Results    ~~~~~~~~~~~~~~~~~~~~~~   \n\n"
                                       + "\t\t    SYSTEM HAS NO SOLUTION."
                                       + "\n\n   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   ");
                        /** Closes the Scanner object. */
                        scan.close();
                        return;
                    }
                    firstIndex = column;
                } else if (lineqs.get(row).getVals()[column] != 0.0) {
                    if (column == variableCount - 1) {
                        hasNonZeroConstant = true;
                    } else {
                        hasVariables = true;
                    }
                }
            }

            /** Given the non-zero terms, the following formats what each variable equals. */
            rowOut = "x_" + (firstIndex + 1) + " = ";
            if (firstIndex == -1 && !hasNonZeroConstant) {
                rowOut += "x_" + (firstIndex + 1);
            } else if (!hasVariables) {
                rowOut += lineqs.get(row).getVals()[variableCount - 1];
            } else {
                for (int column = firstIndex + 1; column < variableCount - 1; column++) {
                    if (lineqs.get(row).getVals()[column] != 0.0) {
                        rowOut += (0 - lineqs.get(row).getVals()[column]) + "x_" + (column + 1) + " + ";
                    }
                }
                if (hasNonZeroConstant) {
                    rowOut += lineqs.get(row).getVals()[variableCount - 1];
                }
            }

            /** The row is added to the overall output String. */
            out += rowOut + "\n\t";
        }

        /** If there are more columns than rows, it will print out the free variables. */
        for (int column = lineqs.size(); column < variableCount - 1; column++) {
            out += "x_" + (column + 1) + " = x_" + (column + 1) + "\n\t";
        }

        out += "\n   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   ";
        System.out.println(out);

        /** Closes the Scanner object. */
        scan.close();
    }

    /**
     * Prints out the linear system in a formatted manner.
     * @param lineqs the linear system to print.
     */
    public static void printSystem(ArrayList<LinEq> lineqs) {
        String out = "";
        for (int index = 0; index < lineqs.size(); index++) {
            out += "\t" + lineqs.get(index).toFormattedString() + (index < lineqs.size() - 1 ? "\n" : "");
        }
        System.out.println(out);
    }
}

/**
 * Class that stores coefficients and constant of a linear equation
 * and can be modified by basic mathematical operations such as
 * scaling (multiplying by constant) and addition (adding another LinEq).
 * The last double is the constant that the linear equation is equal to,
 * and the rest are coefficients.
 */
class LinEq {
    /** The values of the coefficients and constant of the LinEqar equation. */
    private double[] vals;

    /**
     * Makes a LinEq object given the coefficients and constant.
     * @param vals The coefficents and constant of the LinEqar equation.
     */
    public LinEq(double[] vals) {
        this.vals = new double[vals.length];
        for (int index = 0; index < vals.length; index++) {
            this.vals[index] = vals[index];
        }
    }

    /**
     * Creates a deep copy of another LinEq object.
     * @param LinEq LinEq object to copy.
     */
    public LinEq(LinEq linEq) {
        this.vals = new double[linEq.getVals().length];
        for (int index = 0; index < linEq.getVals().length; index++) {
            this.vals[index] = linEq.getVals()[index];
        }
    }

    /** Getter method that returns the coefficients and constant of the LinEq. */
    public double[] getVals() {
        return vals;
    }

    /**
     * Adds a linear equation to the current linear equation.
     * @param LinEq The linear equation to add to the current one.
     * @return an array of doubles representing the summed linear equation.
     */
    public double[] add(LinEq LinEq) {
        double[] otherVals = LinEq.getVals();
        
        if (this.vals.length != otherVals.length) {
            return new double[]{};
        }

        for (int index = 0; index < otherVals.length; index++) {
            this.vals[index] += otherVals[index];
        }
        return vals;
    }

    /**
     * Scales the linear equation by a coefficient.
     * @param scale The scalar to multiply the linear equation by.
     * @return an array of doubles representing the new linear equation.
     */
    public double[] multiply(double scale) {
        for (int index = 0; index < vals.length; index++) {
            this.vals[index] *= scale;
        }
        return vals;
    }

    /**
     * Converts the state of the LinEq into a String.
     */
    public String toString() {
        String out = "[";
        for (int index = 0; index < vals.length; index++) {
            out += vals[index] + ((index < vals.length - 1) ? ", " : "");
        }
        return out + "]";
    }

    /**
     * Converts the state of the LinEq into a formatted String.
     * @return the formatted linear equation.
     */
    public String toFormattedString() {
        String out = "";
        for (int index = 0; index < vals.length; index++) {
            out += Math.abs(vals[index]) + " * ";
            if (index < vals.length - 2) {
                out += "x_" + (index + 1) + (vals[index + 1] < 0 ? " - " : " + ");
            } else if (index < vals.length - 1) {
                out += "x_" + (index + 1) + " = ";
            } else {
                out = out.substring(0, out.length() - 3);
            }
        }
        return out;
    }
}
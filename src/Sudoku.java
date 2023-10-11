import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sudoku {
    //sudoku representation in constraint matrix
    boolean[][] matrix;
    int[][] finalSudoku;
    List<Integer> initialChosenRows;


    /**
     * Sets the constraint matrix for generic sudoku
     */
    private void setMatrix() {
        //each option is 9^2*row+9*col+val (val is 0-8)
        //for each cell
        boolean[][] matrix_new = new boolean[9*9*9][];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                for (int val = 0; val < 9; val++) {
                    boolean[] constaint_participation = new boolean[9*9*4];
                    //unique value per cell constraint
                    constaint_participation[9*row+col] = true;
                    //row constraint (each value only once per row)
                    constaint_participation[9*9+9*row+val] = true;
                    //column constraint
                    constaint_participation[9*9*2+9*col+val] = true;
                    //box constraint
                    constaint_participation[9*9*3+9*(row/3)*3+9*(col/3)+val] = true;
                    matrix_new[9*9*row+9*col+val] = constaint_participation;
                }
            }
        }
        this.matrix = matrix_new;
    }

    /**
     * Asks user to input partial sudoku and derives initial chosen rows in constraint matrix
     */
    public void inputPartialSudoku() {
        //allow user to input partial sudoku, other symbol indicates empty cell
        int[][] sudoku = new int[9][9];
        System.out.println("Please enter partial sudoku, use any symbol for empty cells");
        for (int row = 0; row < 9; row++) {
            String line = new Scanner(System.in).nextLine();
            for (int col = 0; col < 9; col++) {
                if (line.length() != 9) {
                    System.out.println("Invalid line lentgh, please enter 9 characters");
                    row--;
                    break;
                }
                if (line.charAt(col) > '0' && line.charAt(col) <= '9') {
                    sudoku[row][col] = Character.getNumericValue(line.charAt(col));
                }else {
                    sudoku[row][col] = -1;
                }
            }

        }

        //derive initial chosen rows in constraint matrix
        initialChosenRows = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                //if number
                if (sudoku[row][col] > 0) {
                    initialChosenRows.add(9*9*row+9*col+sudoku[row][col]-1);
                }
            }
        }
    }

    /**
     * Calls the solve of the algorithm X and reconstructs + displays the solution
     */
    public void findSolution() {
        //check if input already happended
        if (initialChosenRows.size() == 0) {
            System.out.println("No initial chosen rows, please input partial sudoku");
            return;
        }
        //construct constraint matrix
        this.setMatrix();

        //solve sudoku
        AlgoX algoX = AlgoX.initialSelection(matrix, initialChosenRows);
        algoX = algoX.solve();

        //check if solution found
        if (algoX == null) {
            System.out.println("No solution found");
            return;
        }
        System.out.println("Solution found");

        //reconstruct chosen sudoku numbers
        ReconstructionHelper reconstructionHelper = algoX.reconstructChosenRows();

        //fill final sudoku
        finalSudoku = new int[9][9];
        for (Integer chosenRow : reconstructionHelper.ChosenRows) {
            int row = chosenRow / (9*9);
            int col = (chosenRow % (9*9)) / 9;
            int val = chosenRow % 9;
            finalSudoku[row][col] = val+1;
        }

        //display solved sudoku
        for (int row = 0; row < 9; row++) {
            System.out.println();
            for (int col = 0; col < 9; col++) {
                System.out.print(finalSudoku[row][col]+" ");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Sudoku sudoku = new Sudoku();
        sudoku.inputPartialSudoku();
        sudoku.findSolution();
    }
}

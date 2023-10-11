import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sudoku {
    //sudoku representation in constraint matrix
    boolean[][] matrix;
    int[][] finalSudoku;
    List<Integer> initialChosenRows;

    public void setMatrix() {
        //each option is 9^2*row+9*col+val (val is 0-8, todo has to be shifted for UI)
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

    public void inputPartialSudoku() throws IOException {
        //allow user to input partial sudoku, dot indicates empty cell
        int[][] sudoku = new int[9][9];
        String[] lines = {"746195283",
                "152368.49",
                "389.27651",
                "961243578",
                "478651392",
                "5238,9164",
                "2.4586937",
                "89573,416",
                "637914825"};
        String[] lines2 = {".461.528.",
                "1.23..74.",
                ".89..76..",
                "961.4....",
                "4.8.5..92",
                "...87.1..",
                "21...69.7",
                "89..3....",
                "....14..5"
        };
        for (int row = 0; row < 9; row++) {
            String line = new Scanner(System.in).nextLine();
            for (int col = 0; col < 9; col++) {
                if (line.length() != 9) {
                    System.out.println("Invalid line lentgh, please enter 9 characters");
                    row--;
                    break;
                }
                //if number
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

    public void findSolution() {
        AlgoX algoX = AlgoX.initialSelection(matrix, initialChosenRows);
        algoX = algoX.solve();
        if (algoX == null) {
            System.out.println("No solution found");
            return;
        }
        System.out.println("Solution found");
        ReconstructionHelper reconstructionHelper = algoX.reconstructChosenRows();
        //fill sudoku
        finalSudoku = new int[9][9];
        for (Integer chosenRow : reconstructionHelper.ChosenRows) {
            int row = chosenRow / (9*9);
            int col = (chosenRow % (9*9)) / 9;
            int val = chosenRow % 9;
            finalSudoku[row][col] = val+1;
        }
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
        sudoku.setMatrix();
        sudoku.findSolution();
    }
}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AlgoX {

    boolean[][] matrix;
    List<Integer> selection = null;
    List<Integer> rowDeletions = new ArrayList<>();
    AlgoX parent;
    List<Integer> choices;

    /**
     *
     * @param matrix matrix to solve
     * @param parent previous iteration AlgoX, used for reconstruction
     */
    private AlgoX(boolean[][] matrix, AlgoX parent) {
        this.matrix = matrix;
        this.parent = parent;
        //init choices
        choices = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            choices.add(i);
        }
        //shuffle
        Collections.shuffle(choices);
    }

    /**
     * Creates initial AlgoX object with a given set of chosen rows
     * @param matrix generic starting constraint matrix
     * @param initialChosenRows list of force chosen rows
     * @return
     */
    public static AlgoX initialSelection(boolean[][] matrix, List<Integer> initialChosenRows) {
        //reverse sort initial chosen rows
        AlgoX algoX = new AlgoX(matrix, null);
        algoX = algoX.multiSelectRows(initialChosenRows);
        return algoX;
    }

    /**
     * Selects multiple rows at once
     * @param initialChosenRows list of chosen rows
     * @return new AlgoX object with selected rows removed/selected
     */
    private AlgoX multiSelectRows(List<Integer> initialChosenRows) {
        selection = initialChosenRows;
        rowDeletions = new ArrayList<>(initialChosenRows);
        List<Integer> colsToRemove = new ArrayList<>();
        //for each row
        for (int row : initialChosenRows) {
            //screen all colums
            for (int col = 0; col < matrix[0].length; col++) {
                //if column contains 1
                if (matrix[row][col]) {
                    //add column to remove
                    colsToRemove.add((Integer)col);
                    //screen all rows
                    for (int j = 0; j < matrix.length; j++) {
                        //if row contains 1
                        if (matrix[j][col] && !initialChosenRows.contains(j)) {
                            //add row to remove
                            rowDeletions.add(j);
                        }
                    }
                }
            }
        }
        //unique constraint
        rowDeletions = rowDeletions.stream().distinct().collect(Collectors.toList());
        colsToRemove = colsToRemove.stream().distinct().collect(Collectors.toList());

        //remove rows and columns
        boolean[][] newMatrix = new boolean[matrix.length - rowDeletions.size()][matrix[0].length - colsToRemove.size()];
        int newMatrixRow = 0;
        int newMatrixCol = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (!rowDeletions.contains(i)) {
                for (int j = 0; j < matrix[0].length; j++) {
                    if (!colsToRemove.contains(j)) {
                        newMatrix[newMatrixRow][newMatrixCol] = matrix[i][j];
                        newMatrixCol++;
                    }
                }
                newMatrixRow++;
                newMatrixCol = 0;
            }
        }
        return new AlgoX(newMatrix, this);
    }

    /**
     *
     * @param row row to be selected
     * @return null, if selection leads to dead end
     */
    private AlgoX selectRow(int row) {
        selection = Collections.singletonList(row);
        rowDeletions = new ArrayList<>();
        rowDeletions.add(row);
        List<Integer> colsToRemove = new ArrayList<>();
        //screen all colums
        for (int col = 0; col < matrix[0].length; col++) {
            //if column contains 1
            if (matrix[row][col]) {
                //add column to remove
                colsToRemove.add((Integer)col);
                //screen all rows
                for (int j = 0; j < matrix.length; j++) {
                    //if row contains 1
                    if (matrix[j][col] && j != row) {
                        //add row to remove
                        rowDeletions.add(j);
                    }
                }
            }
        }
        //unique constraint
        rowDeletions = rowDeletions.stream().distinct().collect(Collectors.toList());
        colsToRemove = colsToRemove.stream().distinct().collect(Collectors.toList());

        //check if dead end
        if (matrix.length - rowDeletions.size() == 0 && matrix[0].length - colsToRemove.size() != 0) {
            return null;
        }
        //remove rows and columns
        boolean[][] newMatrix = new boolean[matrix.length - rowDeletions.size()][matrix[0].length - colsToRemove.size()];
        int newMatrixRow = 0;
        int newMatrixCol = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (!rowDeletions.contains(i)) {
                for (int j = 0; j < matrix[0].length; j++) {
                    if (!colsToRemove.contains(j)) {
                        newMatrix[newMatrixRow][newMatrixCol] = matrix[i][j];
                        newMatrixCol++;
                    }
                }
                newMatrixRow++;
                newMatrixCol = 0;
            }
        }
        return new AlgoX(newMatrix, this);
    }

    /**
     *
     * @return true if matrix is empty (=solution is found)
     */
    private boolean isEmpty() {
        return matrix.length == 0;
    }

    /**
     * @return index of first trivial choice, null if no trivial choice
     */
    private Integer findTrivialChoice() {
        for (int col = 0; col < matrix[0].length; col++) {
            int count = 0;
            Integer lastRow = null;
            for (int row = 0; row < matrix.length; row++) {
                if (matrix[row][col]) {
                    count++;
                    lastRow = row;
                }
            }
            //check trivial choice
            if (count == 1) {
                return lastRow;
            }
            //check dead end
            if (count == 0) {
                return -1;
            }
        }
        return null;
    }

    /**
     *
     * @return index of random choice, null if all choices are exhausted
     */
    private Integer findRandChoice() {
        if (choices.isEmpty()) {
            return null;
        }
        return choices.remove(0);
    }

    /**
     * recursive solve
     * @return solution, null if no solution
     */
    public AlgoX solve() {
        if (isEmpty()) {
            return this;
        }
        Integer trivialChoice = findTrivialChoice();
        //check if dead end
        if (trivialChoice == -1) {
            return null;
        }
        if (trivialChoice != null) {
            AlgoX a2 = selectRow(trivialChoice);
            if (a2 == null) {
                return null;
            }
            return a2.solve();
        }
        Integer randChoice;
        while ((randChoice = findRandChoice()) != null) {
            AlgoX a2 = selectRow(randChoice);
            if (a2 == null) {
                continue;
            }
            AlgoX solution = a2.solve();
            if (solution != null) {
                return solution;
            }
        }
        return null;
    }


    /**
     * recursively reconstructs original chosen rows indicees using parents
     *
     * @return list of chosen rows
     */
    public ReconstructionHelper reconstructChosenRows() {
        //final solve produces no choice -> start from parent
        if (selection== null) {
            return parent.reconstructChosenRows();
        }
        //if root, return collected rows
        if (parent == null) {
            ReconstructionHelper helper = new ReconstructionHelper(new ArrayList<>(selection),new ArrayList<>(rowDeletions));
            return helper;
        }
        //if not root, fuse with parent
        ReconstructionHelper parentReconstruction = parent.reconstructChosenRows();
        List<Integer> truelyChosenRows = new ArrayList<>(parentReconstruction.ChosenRows);
        List<Integer> truelyDeletedRows = new ArrayList<>(parentReconstruction.DeletedRows);

        //add selected row
        for (int chosenRow : selection) {
            int oldIndex = findOldIndex(chosenRow,parentReconstruction.DeletedRows);
            truelyChosenRows.add(oldIndex);
        }

        //add deleted rows
        for (int deletedRow : rowDeletions) {
            int oldIndex = findOldIndex(deletedRow,parentReconstruction.DeletedRows);
            truelyDeletedRows.add(oldIndex);
        }
        //return fused
        ReconstructionHelper helper = new ReconstructionHelper(truelyChosenRows,truelyDeletedRows);
        return helper;
    }

    /**
     * finds old index of a row in the original matrix
     * @param newIndex index of row in the new matrix
     * @param deletions list of deleted rows
     * @return index of row in the original matrix
     */
    private int findOldIndex(int newIndex, List<Integer> deletions) {
        int counter = newIndex;
        int minOriginalSize = newIndex + deletions.size()+1;

        //we look for n-th row that wasnt selected
        for (int i = 0; i < minOriginalSize; i++) {
            if (!deletions.contains(i)) {
                counter--;
            }
            if (counter == -1) {
                return i;
            }
        }
        if (counter != -1) {
            throw new RuntimeException("Choice counter should be -1");
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : matrix) {
            for (boolean b : row) {
                sb.append(b ? "1" : "0");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

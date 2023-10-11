import java.util.Arrays;
import java.util.List;

/**
 * Data class for reconstruction of sudoku from constraint matrix
 */
public class ReconstructionHelper {
    List<Integer> ChosenRows;
    List<Integer> DeletedRows;

    public ReconstructionHelper(List<Integer> ChosenRows, List<Integer> DeletedRows) {
        this.ChosenRows = ChosenRows;
        this.DeletedRows = DeletedRows;
    }


    @Override
    public String toString() {
        return Arrays.toString(ChosenRows.toArray());
    }
}

package aoc2025;

import util.Cell;
import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;

public class Day_07 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/07-test.txt"), 21, 40);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/07-main.txt"), 1619, 23607984027985L);


    public static TaskSolution solve(String input) {
        CharField field = CharField.of(input);

        long result1 = 0;
        long[] timesCurr =  new long[field.numCols];
        Cell startCell = field.findFirstCell('S').orElseThrow();
        timesCurr[startCell.col()] = 1;
        for (int row = startCell.row(); row < field.numRows - 1; row++) {
            long[] timesNext = new long[field.numCols];
            for (int col = 0; col < field.numCols; col++) {
                long cnt = timesCurr[col];
                if (cnt > 0) {
                    if (field.get(row + 1, col) == '^') {
                        // No need to check boundaries due to the input having a border
                        timesNext[col + 1] += cnt;
                        timesNext[col - 1] += cnt;
                        result1++;
                    } else {
                        timesNext[col] += cnt;
                    }
                }
            }
            timesCurr = timesNext;
        }
        long result2 = Arrays.stream(timesCurr).sum();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_07::solve, true);
        Helpers.runTask(MAIN, Day_07::solve, true);
    }
}

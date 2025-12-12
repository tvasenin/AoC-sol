package aoc2024;

import util.Cell;
import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Day_20 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/20-test.txt"), 5, 285);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/20-main.txt"), 1404, 1010981);


    private static final int MIN_RANK_TEST_1 = 20;
    private static final int MIN_RANK_TEST_2 = 50;

    private static final int MIN_RANK_MAIN_1 = 100;
    private static final int MIN_RANK_MAIN_2 = 100;

    private static final int MAX_CHEAT_LENGTH_1 = 2;
    private static final int MAX_CHEAT_LENGTH_2 = 20;

    private static boolean tryUpdateWeight(char[][] innerField, int[][] weights, int row, int col, int newValue) {
        if (innerField[row][col] != '#') {
            int oldValue = weights[row][col];
            if (newValue < oldValue) {
                weights[row][col] = newValue;
                return true;
            }
        }
        return false;
    }

    private static int[][] getMinPaths(CharField field, int curRow, int curCol) {
        char[][] innerField = field.field;
        int[][] visited = new int[field.numRows][field.numCols];
        for (int[] row : visited) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        visited[curRow][curCol] = 0;
        int weight = 0;
        boolean hasNext = true;
        while (hasNext) {
            weight++;
            hasNext = tryUpdateWeight(innerField, visited, --curRow, curCol, weight)      // Up
                      || tryUpdateWeight(innerField, visited, ++curRow, --curCol, weight) // Left
                      || tryUpdateWeight(innerField, visited, ++curRow, ++curCol, weight) // Down
                      || tryUpdateWeight(innerField, visited, --curRow, ++curCol, weight);// Right
        }
        return visited;
    }

    private static long getNumAcceptableCheats(CharField field, int[][] minPaths, int maxCheatLength, int minCheatRank) {
        long numAcceptableCheats = 0;
        char[][] innerField = field.field;
        for (int startRow = 1; startRow <= field.numRows - 2; startRow++) {
            for (int startCol = 1; startCol <= field.numCols - 2; startCol++) {
                if (innerField[startRow][startCol] != '#') {
                    int minRow = Math.max(startRow - maxCheatLength, 1);
                    int maxRow = Math.min(startRow + maxCheatLength, field.numRows - 2);
                    for (int endRow = minRow; endRow <= maxRow; endRow++) {
                        int stepsUsed = Math.abs(startRow - endRow);
                        int maxStepsCol = maxCheatLength - stepsUsed;
                        int minCol = Math.max(startCol - maxStepsCol, 1);
                        int maxCol = Math.min(startCol + maxStepsCol, field.numCols - 2);
                        for (int endCol = minCol; endCol <= maxCol; endCol++) {
                            if (innerField[endRow][endCol] != '#') {
                                int startWeight = minPaths[startRow][startCol];
                                int cheatLength = Math.abs(endRow - startRow) + Math.abs(endCol - startCol);
                                int newWeightAfterCheat = startWeight + cheatLength;
                                int oldWeight = minPaths[endRow][endCol];
                                int improvement = oldWeight - newWeightAfterCheat;
                                if (improvement >= minCheatRank) {
                                    numAcceptableCheats++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return numAcceptableCheats;
    }

    public static void runTaskCustom(TaskData taskData, int minRank1, int minRank2, int maxCheatLength1,
                                     int maxCheatLength2, boolean skipTimer) {
        Instant start = Instant.now();
        TaskSolution results = solve(taskData.input(), minRank1, minRank2, maxCheatLength1, maxCheatLength2);
        if (!skipTimer) {
            System.out.printf("Time: %d ms\n", Duration.between(start, Instant.now()).toMillis());
        }
        Helpers.printResults(taskData, results);
    }

    public static TaskSolution solve(String input, int minRank1, int minRank2, int maxCheatLength1, int maxCheatLength2) {
        CharField field = CharField.of(input);
        Cell startCell = field.findFirstCell('S').orElseThrow();

        int[][] minPaths = getMinPaths(field, startCell.row(), startCell.col());

        long result1 = getNumAcceptableCheats(field, minPaths, maxCheatLength1, minRank1);
        long result2 = getNumAcceptableCheats(field, minPaths, maxCheatLength2, minRank2);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        runTaskCustom(TEST, MIN_RANK_TEST_1, MIN_RANK_TEST_2, MAX_CHEAT_LENGTH_1, MAX_CHEAT_LENGTH_2, false);
        runTaskCustom(MAIN, MIN_RANK_MAIN_1, MIN_RANK_MAIN_2, MAX_CHEAT_LENGTH_1, MAX_CHEAT_LENGTH_2, false);
    }
}

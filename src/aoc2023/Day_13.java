package aoc2023;

import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day_13 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/13-test.txt"), 405, 400);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/13-main.txt"), 33735, 38063);


    static CharField transpose(CharField field) {
        CharField transposed = new CharField(field.numCols, field.numRows, (char) 0);
        char[][] innerField = field.field;
        char[][] innerTransposed = transposed.field;
        for (int i = 0; i < field.numRows; i++) {
            for (int j = 0; j < field.numCols; j++) {
                innerTransposed[j][i] = innerField[i][j];
            }
        }
        return transposed;
    }

    static boolean isReflection(CharField field, int rowsBefore) {
        boolean isReflection = true;
        int i = rowsBefore - 1;
        int j = rowsBefore;
        char[][] innerField = field.field;
        while (i >= 0 && j < field.numRows) {
            if (!Arrays.equals(innerField[i], innerField[j])) {
                isReflection = false;
                break;
            }
            i--;
            j++;
        }
        return isReflection;
    }

    static List<Integer> getRowReflections(CharField field) {
        List<Integer> refRows = new ArrayList<>();
        for (int rowsBefore = 1; rowsBefore < field.numRows; rowsBefore++) {
            if (isReflection(field, rowsBefore)) {
                refRows.add(rowsBefore);
            }
        }
        return refRows;
    }

    private static void flip(CharField field, int i, int j) {
        field.field[i][j] = field.field[i][j] == '#' ? '.' : '#';
    }

    private static int getSmudged(CharField field, List<Integer> refRows, List<Integer> refCols) {
        int sum = 0;
        for (int i = 0; i < field.numRows; i++) {
            for (int j = 0; j < field.numCols; j++) {
                flip(field, i, j);
                List<Integer> refSmudgedRows = getRowReflections(field);
                List<Integer> refSmudgedCols = getRowReflections(transpose(field));
                flip(field, i, j);
                List<Integer> refSmudgedRowsNew = new ArrayList<>(refSmudgedRows);
                refSmudgedRowsNew.removeAll(refRows);
                List<Integer> refSmudgedColsNew = new ArrayList<>(refSmudgedCols);
                refSmudgedColsNew.removeAll(refCols);
                if (!refSmudgedRowsNew.isEmpty() || !refSmudgedColsNew.isEmpty()) {
                    sum += 100 * refSmudgedRowsNew.stream().mapToInt(e -> e).sum();
                    sum += refSmudgedColsNew.stream().mapToInt(e -> e).sum();
                    return sum;
                }
            }
        }
        return 0;
    }

    public static TaskSolution solve(String input) {
        List<CharField> items = new ArrayList<>();
        for (String block : input.split("\n\n")) {
            items.add(CharField.of(block));
        }

        long result1 = 0;
        long result2 = 0;

        for (CharField item : items) {
            List<Integer> refRows = getRowReflections(item);
            result1 += 100L * refRows.stream().mapToInt(e -> e).sum();

            CharField cols = transpose(item);
            List<Integer> refCols = getRowReflections(cols);
            result1 += refCols.stream().mapToInt(e -> e).sum();

            result2 += getSmudged(item, refRows, refCols);
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_13::solve, true);
        Helpers.runTask(MAIN, Day_13::solve, false);
    }
}

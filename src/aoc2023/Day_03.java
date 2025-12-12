package aoc2023;

import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day_03 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/03-test.txt"), 4361, 467835);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/03-main.txt"), 539637, 82818007);


    private static int parseInt(char[] rowArr, int from, int to) {
        // FIXME: Do not wrap into CharBuffer
        return Integer.parseUnsignedInt(CharBuffer.wrap(rowArr), from, to, 10);
    }

    static boolean containsSymbol(char[] rowArr, int offset, int length) {
        for (int i = 0; i < length; i++) {
            final char ch = rowArr[offset + i];
            if (ch != '.' && !Character.isDigit(ch)) {
                return true;
            }
        }
        return false;
    }

    static boolean isPartNumber(CharField field, int row, int beginIndex, int endIndex) {
        char[] curr = field.field[row];
        int start = Math.max(beginIndex - 1, 0);
        int end = Math.min(endIndex + 1, field.numCols);
        boolean hasAdjacentSymbol = false;
        if (row > 0) {
            char[] prev = field.field[row - 1];
            hasAdjacentSymbol = containsSymbol(prev, start, end - start);
        }
        hasAdjacentSymbol = hasAdjacentSymbol || containsSymbol(curr, start, end - start);
        if (row < field.numRows - 1) {
            char[] next = field.field[row + 1];
            hasAdjacentSymbol = hasAdjacentSymbol || containsSymbol(next, start, end - start);
        }
        return hasAdjacentSymbol;
    }

    static int scanNumber(char[] rowArr, int col) {
        if (!Character.isDigit(rowArr[col])) {
            throw new RuntimeException("Not a digit at the position " + col + " at row " + Arrays.toString(rowArr));
        }
        int beginIndex = col;
        while (beginIndex > 0 && Character.isDigit(rowArr[beginIndex - 1])) {
            beginIndex--;
        }
        int endIndex = col + 1;
        while (endIndex < rowArr.length && Character.isDigit(rowArr[endIndex])) {
            endIndex++;
        }
        return parseInt(rowArr, beginIndex, endIndex);
    }

    static List<Integer> scanNumbers(CharField field, int row, int col) {
        char[] rowArr = field.field[row];
        List<Integer> partNumbers = new ArrayList<>();
        if (Character.isDigit(rowArr[col])) {
            partNumbers.add(scanNumber(rowArr, col));
        } else {
            if (Character.isDigit(rowArr[col - 1])) {
                partNumbers.add(scanNumber(rowArr, col - 1));
            }
            if (Character.isDigit(rowArr[col + 1])) {
                partNumbers.add(scanNumber(rowArr, col + 1));
            }
        }
        return partNumbers;
    }

    static int getGearRatio(CharField field, int row, int col) {
        List<Integer> partNumbers = new ArrayList<>();
        if (row > 0) {
            partNumbers.addAll(scanNumbers(field, row - 1, col));
        }
        partNumbers.addAll(scanNumbers(field, row, col));
        if (row < field.numRows - 1) {
            partNumbers.addAll(scanNumbers(field, row + 1, col));
        }
        if (partNumbers.size() == 2) {
            return partNumbers.get(0) * partNumbers.get(1);
        } else {
            return 0;
        }
    }

    public static TaskSolution solve(String input) {
        CharField field = CharField.of(input);

        long result1 = 0, result2 = 0;
        for (int r = 0; r < field.numRows; r++) {
            char[] rowArr = field.field[r];
            for (int c = 0; c < rowArr.length; c++) {
                if (Character.isDigit(rowArr[c])) {
                    int beginIndex = c;
                    while (c < rowArr.length && Character.isDigit(rowArr[c])) {
                        c++;
                    }
                    int endIndex = c;
                    if (isPartNumber(field, r, beginIndex, endIndex)) {
                        result1 += parseInt(rowArr, beginIndex, endIndex);
                    }
                }
            }
            for (int c = 0; c < rowArr.length; c++) {
                if (rowArr[c] == '*') {
                    result2 += getGearRatio(field, r, c);
                }
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_03::solve, true);
        Helpers.runTask(MAIN, Day_03::solve, true);
    }
}

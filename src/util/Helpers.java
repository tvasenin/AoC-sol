package util;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Stream;

public class Helpers {

    public static char[][] readCharField(String input) {
        String[] lines = input.split("\n");
        int numRows = lines.length;
        int numCols = lines[0].length();
        char[][] field = new char[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            String line = lines[i];
            field[i] = line.toCharArray();
        }
        return field;
    }
    public static int[][] readIntField(String input) {
        String[] lines = input.split("\n");
        int numRows = lines.length;
        int numCols = lines[0].length();
        int[][] field = new int[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            String line = lines[i];
            field[i] = line.chars().map(Character::getNumericValue).toArray();
        }
        return field;
    }

    public static Cell findFirstOnField(char[][] field, char charToFind) {
        int numRows = field.length;
        int numCols = field[0].length;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (field[i][j] == charToFind) {
                    return new Cell(i, j);
                }
            }
        }
        throw new IllegalArgumentException("Char " + charToFind + " was not found on the char field!");
    }

    public static void printCharField(char[][] field) {
        System.out.println();
        for (char[] chars : field) {
            System.out.println(new String(chars));
        }
    }

    public static void printCharField(CharField field) {
        System.out.println();
        for (char[] chars : field.field) {
            System.out.println(new String(chars));
        }
    }

    public static void printCharField(CharFieldLinear field) {
        System.out.println();
        int stride = field.stride;
        char[] buf = new char[stride];
        int srcPos = 0;
        for (int i = 0; i < field.numRows; i++) {
            System.arraycopy(field.field, srcPos, buf, 0, stride);
            srcPos += field.stride;
            System.out.println(new String(buf));
        }
    }

    public static Stream<Cell> getNeighborsULRD(int row, int col) {
        return Stream.of(new Cell(row-1, col), new Cell(row, col-1), new Cell(row, col+1), new Cell(row+1, col));
    }

    public static Stream<Cell> getNeighbors8(int row, int col) {
        return Stream.of(
                new Cell(row-1, col-1), new Cell(row-1, col), new Cell(row-1, col+1),
                new Cell(row, col-1), new Cell(row, col+1),
                new Cell(row+1, col-1), new Cell(row+1, col), new Cell(row+1, col+1)
        );
    }

    public static Stream<Cell> getValidNeighborsStream(Cell cell, int numRows, int numCols, boolean onlyULRD) {
        return getValidNeighborsStream(cell.row(), cell.col(), numRows, numCols, onlyULRD);
    }

    public static Stream<Cell> getValidNeighborsStream(int row, int col, int numRows, int numCols, boolean onlyULRD) {
        Stream<Cell> neighborsStream = onlyULRD ? getNeighborsULRD(row, col) : getNeighbors8(row, col);
        return neighborsStream
                .filter(cell -> isWithinField(cell, numRows, numCols));
    }

    public static Cell getNextCell(Cell cell, Direction direction) {
        return getNextCell(cell, direction, 1);
    }

    public static Cell getNextCell(Cell cell, Direction direction, int numSteps) {
        return switch (direction) {
            case U -> new Cell(cell.row() - numSteps, cell.col());
            case L -> new Cell(cell.row(), cell.col() - numSteps);
            case D -> new Cell(cell.row() + numSteps, cell.col());
            case R -> new Cell(cell.row(), cell.col() + numSteps);
        };
    }

    public static boolean isWithinField(Cell cell, int numRows, int numCols) {
        return -1 < cell.row() && cell.row() < numRows && -1 < cell.col() && cell.col() < numCols;
    }

    public static Integer[] parseToIntArrayBoxed(String input, String delimiterPattern) {
        return getTokens(input, delimiterPattern).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
    }

    protected static Stream<String> getTokens(String input, String delimiterPattern) {
        return new Scanner(input).useDelimiter(delimiterPattern).tokens();
    }

    public static int[] parseCommaDelimitedIntArray(String input) {
        return parseIntArray(input, ",");
    }

    public static int[] parseIntArray(String input, String delimiterRegex) {
        return Arrays.stream(input.split(delimiterRegex)).mapToInt(Integer::parseInt).toArray();
    }

    private static String formatResultStatus(long result, long expectedResult) {
        boolean isWrongResult = result != expectedResult;
        StringBuilder sb = new StringBuilder();
        sb.append(isWrongResult ? ConsoleColors.RED : ConsoleColors.GREEN).append(result).append(ConsoleColors.RESET);
        if (isWrongResult) {
            sb.append(" (should be ").append(ConsoleColors.YELLOW).append(expectedResult).append(ConsoleColors.RESET).append(")");
        }
        return sb.toString();
    }

    public static void printResults(TaskData taskData, TaskSolution sol) {
        System.out.println("Result (Part 1): " + formatResultStatus(sol.result1(), taskData.result().result1()));
        System.out.println("Result (Part 2): " + formatResultStatus(sol.result2(), taskData.result().result2()));
        String overallStatus = sol.result1() == taskData.result().result1() && sol.result2() == taskData.result().result2()
                ? ConsoleColors.GREEN + "OK" + ConsoleColors.RESET
                : ConsoleColors.RED + "FAIL" + ConsoleColors.RESET;
        System.out.println(overallStatus);
    }

    public static void printResults(String input, String expectedInput, long result1, long expectedResult1,
                                    long result2, long expectedResult2) {
        if (input.equals(expectedInput)) {
            if (result1 != expectedResult1) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != expectedResult2) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }

    public static TaskSolution runTask(TaskData taskData, Function<String, TaskSolution> solve) {
        return runTask(taskData, solve, false);
    }

    public static TaskSolution runTask(TaskData taskData, Function<String, TaskSolution> solve, boolean skipTimer) {
        Instant start = Instant.now();
        TaskSolution results = solve.apply(taskData.input());
        if (!skipTimer) {
            System.out.printf("Time: %d ms\n", Duration.between(start, Instant.now()).toMillis());
        }
        Helpers.printResults(taskData, results);
        return results;
    }
}

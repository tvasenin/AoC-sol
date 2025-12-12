package util;

import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.StringWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public static <V, E> String exportGraphDOT(Graph<V, E> graph) {
        DOTExporter<V, E> exporter = new DOTExporter<>(Object::toString);
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            if (!(v instanceof String)) {
                map.put("label", DefaultAttribute.createAttribute(v.toString()));
            }
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        return writer.toString();
    }

    public static Cell[] getNeighborsULRD(int row, int col) {
        return new Cell[] { new Cell(row-1, col), new Cell(row, col-1), new Cell(row, col+1), new Cell(row+1, col) };
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
        Stream<Cell> neighborsStream = onlyULRD ? Arrays.stream(getNeighborsULRD(row, col)) : getNeighbors8(row, col);
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

    public static int[] parseCommaDelimitedIntArray(String input) {
        return parseIntArray(input, ",");
    }

    public static int[] parseIntArray(String input, String delimiterRegex) {
        return Arrays.stream(input.split(delimiterRegex)).mapToInt(Integer::parseInt).toArray();
    }

    private static String formatResultStatus(String expectedResult, String result) {
        boolean isWrongResult = !Objects.equals(result, expectedResult);
        StringBuilder sb = new StringBuilder();
        sb.append(isWrongResult ? ConsoleColors.RED : ConsoleColors.GREEN).append(result).append(ConsoleColors.RESET);
        if (isWrongResult) {
            sb.append(" (expected ").append(ConsoleColors.YELLOW).append(expectedResult).append(ConsoleColors.RESET).append(")");
        }
        return sb.toString();
    }

    public static void printResults(TaskData taskData, TaskSolution sol) {
        printResults(taskData.result(), sol);
    }

    public static void printResults(TaskSolution expected, TaskSolution actual) {
        printResults(expected.result1(), actual.result1(), expected.result2(), actual.result2());
    }

    public static void printResults(String expectedResult1, String result1, String expectedResult2, String result2) {
        System.out.println("Result (Part 1): " + formatResultStatus(expectedResult1, result1));
        System.out.println("Result (Part 2): " + formatResultStatus(expectedResult2, result2));
        String overallStatus = Objects.equals(expectedResult1, result1) && Objects.equals(expectedResult2, result2)
                ? ConsoleColors.GREEN + "OK" + ConsoleColors.RESET
                : ConsoleColors.RED + "FAIL" + ConsoleColors.RESET;
        System.out.println(overallStatus);
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

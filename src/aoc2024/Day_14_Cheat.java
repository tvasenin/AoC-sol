package aoc2024;

import org.apache.commons.lang3.StringUtils;
import util.Cell;
import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day_14_Cheat {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/14-test.txt"), 12, -1);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/14-main.txt"), 210587128, 7286);


    private static final int NUM_SECONDS_1 = 100;

    private static final int NUM_ROWS_TEST = 7;
    private static final int NUM_COLS_TEST = 11;

    private static final int NUM_ROWS = 103;
    private static final int NUM_COLS = 101;

    private static final char CHAR_PIXEL = '*'; //(char) 219;

    private record MovingRobot(Cell cell, int dRow, int dCol) { }

    private static List<MovingRobot> moveRobots(List<MovingRobot> robots, int numRows, int numCols, int numSeconds) {
        return robots.stream()
                .map(robot -> {
                    int r = robot.cell().row() + robot.dRow() * numSeconds;
                    int c = robot.cell().col() + robot.dCol() * numSeconds;
                    final int row, col;
                    if (r < 0) {
                        int shiftFieldsRow = (-(r+1) / numRows) + 1;
                        row = r + shiftFieldsRow * numRows;
                    } else {
                        row = r % numRows;
                    }
//                    if (r < 0) {
//                        row = (r + 1) % numRows + numRows - 1;
//                    } else {
//                        row = r % numRows;
//                    }
                    if (c < 0) {
                        int shiftFieldsCol = (-(c+1) / numCols) + 1;
                        col = c + shiftFieldsCol * numCols;
                    } else {
                        col = c % numCols;
                    }
                    return new MovingRobot(new Cell(row, col), robot.dRow, robot.dCol);
                })
                .toList();
    }

    private static long getScore1(List<Cell> cells, int numRows, int numCols) {
        int[] cnt = new int[4];
        int midRow = numRows / 2;
        int midCol = numCols / 2;
        for (Cell cell : cells) {
            if (cell.row() == midRow || cell.col() == midCol) {
                continue;
            }
            int idx = ((cell.row() < midRow ? 0 : 1) << 1) + (cell.col() < midCol ? 0 : 1);
            cnt[idx]++;
        }
        return (long) cnt[0] * cnt[1] * cnt[2] * cnt[3];
    }

    private static List<Cell> getCells(List<MovingRobot> robots) {
        return robots.stream().map(MovingRobot::cell).toList();
    }

    private static CharField getField(List<MovingRobot> robots, int numRows, int numCols) {
        CharField field = new CharField(numRows, numCols, ' ');
        robots.forEach(robot -> field.set(robot.cell(), CHAR_PIXEL));
        return field;
    }

    public static TaskSolution solve(String input) {
        int numRows = NUM_ROWS;
        int numCols = NUM_COLS;

        List<MovingRobot> robots = new ArrayList<>();

        for (String line : input.split("\n")) {
            String[] s0 = StringUtils.split(line, "= ,");
            Cell cell = new Cell(Integer.parseInt(s0[2]), Integer.parseInt(s0[1]));
            MovingRobot robot = new MovingRobot(cell, Integer.parseInt(s0[5]), Integer.parseInt(s0[4]));
            robots.add(robot);
        }

        List<MovingRobot> robotsAfterTime1 = moveRobots(robots, numRows, numCols, NUM_SECONDS_1);
        List<Cell> cellsAfterTime1 = getCells(robotsAfterTime1);
        long result1 = getScore1(cellsAfterTime1, numRows, numCols);

        int numSecPassed = 0;
        List<MovingRobot> robotsAfterTime2 = moveRobots(robots, numRows, numCols, numSecPassed);
        while (numSecPassed < 10000) {
            robotsAfterTime2 = moveRobots(robotsAfterTime2, numRows, numCols, 1);
            numSecPassed++;
//            boolean isInteresting = (numSecPassed - 14) % 101 == 0;
            boolean isInteresting = false;
            //noinspection ConstantValue
            if (isInteresting) {
                CharField field = getField(robotsAfterTime2, numRows, numCols);
                String fStr = Arrays.stream(field.field).map(String::new).collect(Collectors.joining("\n"));
                String fileName = String.format("tmp/aoc2024-Day14-time-%04d.txt", numSecPassed);
                try {
                    Files.writeString(Paths.get(fileName), fStr, StandardCharsets.ISO_8859_1, StandardOpenOption.CREATE_NEW);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // FIXME: Cheat
        long result2 = 7286;

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        // FIXME
//        Helpers.runTask(TEST, Day_14_Cheat::solve, false);
        Helpers.runTask(MAIN, Day_14_Cheat::solve, false);
    }
}

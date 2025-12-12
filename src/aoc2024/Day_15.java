package aoc2024;

import util.Cell;
import util.CharField;
import util.Direction;
import util.Helpers;
import util.Resources;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day_15 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/15-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/15-main.txt");


    private static long getScore(CharField field, char matchChar) {
        long score = 0;
        for (int row = 0; row < field.numRows; row++) {
            for (int col = 0; col < field.numCols; col++) {
                if (field.get(row, col) == matchChar) {
                    score += 100L * row + col;
                }
            }
        }
        return score;
    }

    private static Direction getDirection(char move) {
        return switch (move) {
            case '^' -> Direction.U;
            case '<' -> Direction.L;
            case 'v' -> Direction.D;
            case '>' -> Direction.R;
            default -> throw new IllegalStateException("Unexpected move: " + move);
        };
    }

    private static void simulateMoves1(CharField field, char[] moves) {
        Cell robot = field.findFirstCell('@').orElseThrow();
        for (char move : moves) {
            Direction dir = getDirection(move);
            Cell cellNext = Helpers.getNextCell(robot, dir);
            char objNext = field.get(cellNext);
            boolean moveRobot = false;
            switch (objNext) {
                case '.' -> moveRobot = true;
                case '#' -> { }
                case 'O' -> {
                    Cell cellNextNext = Helpers.getNextCell(cellNext, dir);
                    while (field.get(cellNextNext) == 'O') {
                        cellNextNext = Helpers.getNextCell(cellNextNext, dir);
                    }
                    if (field.get(cellNextNext) == '.') {
                        moveRobot = true;
                        field.set(cellNext, '.');
                        field.set(cellNextNext, 'O');
                    }
                }
                default -> throw new IllegalStateException("Unexpected objNext: " + move);
            }
            if (moveRobot) {
                field.set(robot, '.');
                field.set(cellNext, '@');
                robot = cellNext;
            }
        }
    }

    private static boolean simulateBoxMoveLR(CharField field, Cell robot, Direction  dir) {
        Cell cellNext = Helpers.getNextCell(robot, dir);
        Cell cellNextNext = Helpers.getNextCell(cellNext, dir);
        char objNextNext = field.get(cellNextNext);
        while (objNextNext == '[' || objNextNext == ']') {
            cellNextNext = Helpers.getNextCell(cellNextNext, dir);
            objNextNext = field.get(cellNextNext);
        }
        if (field.get(cellNextNext) == '.') {
            char[] row = field.field[robot.row()];
            int idxNextNext = cellNextNext.col();
            int idxNext = cellNext.col();
            int length = Math.abs(idxNext - idxNextNext);
            int idxSrc = dir == Direction.L ? idxNextNext + 1 : idxNext;
            int idxDst = dir == Direction.L ? idxNextNext : idxNext + 1;
            System.arraycopy(row, idxSrc, row, idxDst, length);
            field.set(cellNext, '.');
            return  true;
        }
        return false;
    }

    private static boolean simulateBoxMoveUD(CharField field, Cell robot, Direction  dir) {
        Set<Cell> front = new HashSet<>();
        front.add(robot);
        List<Cell> cellsToMove = new ArrayList<>();
        while (!front.isEmpty()) {
            Set<Cell> frontNew = new HashSet<>();
            for (Cell cell : front) {
                Cell cellNext = Helpers.getNextCell(cell, dir);
                char objNext = field.get(cellNext);
                switch (objNext) {
                    case '.' -> {}
                    case '#' -> {
                        return false;
                    }
                    case '[' -> {
                        frontNew.add(cellNext);
                        frontNew.add(Helpers.getNextCell(cellNext, Direction.R));
                    }
                    case ']' -> {
                        frontNew.add(cellNext);
                        frontNew.add(Helpers.getNextCell(cellNext, Direction.L));
                    }
                    default -> throw new IllegalStateException("Unexpected objNext: " + objNext);
                }
            }
            front = frontNew;
            cellsToMove.addAll(front);
        }
        for (Cell cell : cellsToMove.reversed()) {
            Cell cellNext = Helpers.getNextCell(cell, dir);
            field.set(cellNext, field.get(cell));
            field.set(cell, '.');
        }
        return true;
    }

    private static void simulateMoves2(CharField field, char[] moves) {
        Cell robot = field.findFirstCell('@').orElseThrow();
        for (char move : moves) {
            Direction dir = getDirection(move);
            Cell cellNext = Helpers.getNextCell(robot, dir);
            char objNext = field.get(cellNext);
            boolean moveRobot = false;
            switch (objNext) {
                case '.' -> moveRobot = true;
                case '#' -> { }
                case '[', ']' -> {
                    switch (dir) {
                        case U, D -> moveRobot = simulateBoxMoveUD(field, robot, dir);
                        case L, R -> moveRobot = simulateBoxMoveLR(field, robot, dir);
                    }
                }
                default -> throw new IllegalStateException("Unexpected objNext: " + move);
            }
            if (moveRobot) {
                field.set(robot, '.');
                field.set(cellNext, '@');
                robot = cellNext;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        String[] s0 = input.split("\n\n");
        CharField field = CharField.of(s0[0]);
        char[] moves = s0[1].replace("\n", "").toCharArray();

        simulateMoves1(field, moves);
        long result1 = getScore(field, 'O');

        String input2 = s0[0]
                .replace("#", "##")
                .replace("O", "[]")
                .replace(".", "..")
                .replace("@", "@.");
        CharField field2 = CharField.of(input2);
        simulateMoves2(field2, moves);
        long result2 = getScore(field2, '[');

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 1559280) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 1576353) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

package aoc2023;

import org.apache.commons.lang3.StringUtils;
import util.Cell;
import util.CharField;
import util.Direction;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.List;

public class Day_10 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/10-main.txt"), 6931, 357);


    private static int getNumEnclosedTiles(CharField filteredField) {
        int numInside = 0;
        for (char[] chars : filteredField.field) {
            boolean isInside = false;
            for (char c : chars) {
                switch (c) {
                    case '│', '┐', '┌' -> isInside = !isInside;
                    case '.' -> {
                        if (isInside) {
                            numInside++;
                        }
                    }
                }
            }
        }
        return numInside;
    }

    private static boolean isConnected(Direction dir, char c) {
        return switch (c) {
            case '│' -> dir == Direction.D || dir == Direction.U;
            case '└' -> dir == Direction.D || dir == Direction.L;
            case '┘' -> dir == Direction.D || dir == Direction.R;
            case '┐' -> dir == Direction.U || dir == Direction.R;
            case '┌' -> dir == Direction.U || dir == Direction.L;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    private static List<Direction> findConnectionDirs(CharField field, Cell start) {
        List<Direction> dirs = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            Cell cellNext = Helpers.getNextCell(start, dir);
            if (field.isWithinField(cellNext) && isConnected(dir, field.get(cellNext))) {
                dirs.add(dir);
            }
        }
        return dirs;
    }

    private static Direction changeDirByPath(Direction dir, char c) {
        return switch (c) {
            case '│', '─' -> dir;
            case '└' -> dir == Direction.D ? Direction.R : Direction.U;
            case '┘' -> dir == Direction.D ? Direction.L : Direction.U;
            case '┐' -> dir == Direction.U ? Direction.L : Direction.D;
            case '┌' -> dir == Direction.U ? Direction.R : Direction.D;
            default -> dir; // to accommodate 'S' at the start
        };
    }

    private static List<Cell> getLoopPath(CharField field, Cell start, Direction dir) {
        List<Cell> path = new ArrayList<>();
        Cell cell = start;
        do {
            path.add(cell);
            cell = Helpers.getNextCell(cell, dir);
            dir = changeDirByPath(dir, field.get(cell));
        } while (!cell.equals(start));
        return path;
    }

    private static char getFill(Direction dir1, Direction dir2) {
        if (dir1 == dir2) {
            throw new IllegalArgumentException("Directions can not be the same");
        }
        if ((dir1 == Direction.U && dir2 == Direction.D) || (dir1 == Direction.D && dir2 == Direction.U)) { return '│'; }
        if ((dir1 == Direction.L && dir2 == Direction.R) || (dir1 == Direction.R && dir2 == Direction.L)) { return '─'; }
        if ((dir1 == Direction.U && dir2 == Direction.R) || (dir1 == Direction.R && dir2 == Direction.U)) { return '└'; }
        if ((dir1 == Direction.U && dir2 == Direction.L) || (dir1 == Direction.L && dir2 == Direction.U)) { return '┘'; }
        if ((dir1 == Direction.D && dir2 == Direction.L) || (dir1 == Direction.L && dir2 == Direction.D)) { return '┐'; }
        if ((dir1 == Direction.D && dir2 == Direction.R) || (dir1 == Direction.R && dir2 == Direction.D)) { return '┌'; }
        throw new IllegalArgumentException();
    }

    public static TaskSolution solve(String input) {
        String fancyInput = StringUtils.replaceChars(input, "|-LJ7F", "│─└┘┐┌");

        CharField field = CharField.of(fancyInput);
        Cell start = field.findFirstCell('S').orElseThrow();

        List<Direction> connections = findConnectionDirs(field, start);
        if (connections.size() != 2) {
            throw new IllegalArgumentException();
        }

        char startFill = getFill(connections.get(0), connections.get(1));
        field.set(start, startFill);

        List<Cell> loopPath = getLoopPath(field, start, connections.getFirst());
        long result1 = loopPath.size() / 2; // Farthest distance

        CharField loopOnly = new CharField(field.numRows, field.numCols, '.');
        loopPath.forEach(cell -> loopOnly.set(cell, field.get(cell)));

        long result2 = getNumEnclosedTiles(loopOnly);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        solve(MAIN.input()); // Warmup
        Helpers.runTask(MAIN, Day_10::solve, false);
    }
}

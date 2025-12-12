package aoc2023;

import org.apache.commons.lang3.StringUtils;
import util.Cell;
import util.CharField;
import util.Direction;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

public class Day_18 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/18-test.txt"), 62, 952408144115L);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/18-main.txt"), 61865, 40343619199142L);


    record Command(Direction dir, int len) { }

    static void fillEdge(CharField field, Cell cell1, Cell cell2) {
        if (cell1.row() == cell2.row()) {
            int row = cell1.row();
            int colFrom = Math.min(cell1.col(), cell2.col());
            int colTo = Math.max(cell1.col(), cell2.col());
            for (int col = colFrom; col <= colTo ; col++) {
                field.set(row, col, '#');
            }
        } else if (cell1.col() == cell2.col()) {
            int col = cell1.col();
            int rowFrom = Math.min(cell1.row(), cell2.row());
            int rowTo = Math.max(cell1.row(), cell2.row());
            for (int row = rowFrom; row <= rowTo ; row++) {
                field.set(row, col, '#');
            }
        } else {
            throw new RuntimeException("not horizontal/vertical line");
        }
    }

    static void fillPolygon(CharField field) {
        int numRows = field.numRows;
        int numCols = field.numCols;
        Queue<Cell> queue = new ArrayDeque<>();
        // Add outer cells
        for (int row = 0; row < numRows; row++) {
            if (field.get(row, 0) == '.') {
                queue.add(new Cell(row, 0));
            }
            if (field.get(row, numCols - 1) == '.') {
                queue.add(new Cell(row, numCols - 1));
            }
        }
        for (int col = 0; col < numCols; col++) {
            if (field.get(0, col) == '.') {
                queue.add(new Cell(0, col));
            }
            if (field.get(numRows - 1, col) == '.') {
                queue.add(new Cell(numRows - 1, col));
            }
        }
        queue.forEach(cell -> field.set(cell, ' '));

        // Flood fill blanks
        while (!queue.isEmpty()) {
            Cell currCell = queue.remove();
            Stream<Cell> validNeighbors = Helpers.getValidNeighborsStream(currCell, numRows, numCols, false);
            List<Cell> pendingNeighbors = validNeighbors.filter(cell -> field.get(cell) == '.').toList();
            pendingNeighbors.forEach(cell -> field.set(cell, ' '));
            queue.addAll(pendingNeighbors);
        }
    }

    private static List<Cell> getNormalizedPolygon(List<Command> commands) {
        int row = 0, col = 0;
        List<Cell> rawPolygon = new ArrayList<>(commands.size());
        for (Command command : commands) {
            switch (command.dir) {
                case U -> row -= command.len();
                case D -> row += command.len();
                case L -> col -= command.len();
                case R -> col += command.len();
            }
            rawPolygon.add(new Cell(row, col));
        }
        int minRow = rawPolygon.stream().mapToInt(Cell::row).min().orElseThrow();
        int minCol = rawPolygon.stream().mapToInt(Cell::col).min().orElseThrow();
        return rawPolygon.stream()
                .map(cell -> new Cell(cell.row() - minRow, cell.col() - minCol))
                .toList();
    }


    private static long det(int a, int b, int c, int d) {
        return (long) a * d - (long) b * c;
    }

    private static long getNumInternalCells(List<Command> commands) {
        int perimeter = commands.stream().mapToInt(Command::len).sum();
        List<Cell> polygon = getNormalizedPolygon(commands);
        long area2x = 0;
        for (int i = 0; i < polygon.size(); i++) {
            Cell cell1 = polygon.get(i);
            Cell cell2 = polygon.get((i + 1) % polygon.size());
            area2x += det(cell1.col(), cell2.col(), cell1.row(), cell2.row());
        }
        return (Math.abs(area2x) + perimeter) / 2 + 1;
    }

    private static Command getCommandFromFakeColor(String fakeColor) {
        char c = fakeColor.charAt(fakeColor.length() - 1);
        Direction dir = switch (c) {
            case '0' -> Direction.R;
            case '1' -> Direction.D;
            case '2' -> Direction.L;
            case '3' -> Direction.U;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
        int len = Integer.parseInt(fakeColor.substring(0, fakeColor.length() - 1), 16);
        return new Command(dir, len);
    }

    public static TaskSolution solve(String input) {
        List<Command> commands1 = new ArrayList<>();
        List<Command> commands2 = new ArrayList<>();
        input.lines().forEach(line -> {
            String[] s0 = StringUtils.split(line, " (#)");
            Command command1 = new Command(Direction.valueOf(s0[0]), Integer.parseInt(s0[1]));
            commands1.add(command1);
            commands2.add(getCommandFromFakeColor(s0[2]));
        });
        List<Cell> polygon1 = getNormalizedPolygon(commands1);

        int numRows = polygon1.stream().mapToInt(Cell::row).max().orElseThrow() + 1;
        int numCols = polygon1.stream().mapToInt(Cell::col).max().orElseThrow() + 1;
        CharField field = new CharField(numRows, numCols, '.');
        for (int i = 0; i < polygon1.size(); i++) {
            fillEdge(field, polygon1.get(i), polygon1.get((i+1) % polygon1.size()));
        }
        fillPolygon(field);

        long result1 = getNumInternalCells(commands1);
        long result2 = getNumInternalCells(commands2);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_18::solve, false);
        Helpers.runTask(MAIN, Day_18::solve, false);
    }
}

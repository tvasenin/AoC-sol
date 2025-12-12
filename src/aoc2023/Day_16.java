package aoc2023;

import util.CharFieldLinear;
import util.Direction;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Day_16 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/16-test.txt"), 46, 51);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/16-main.txt"), 8112, 8314);


    private record Beam(int cell, Direction dir) { }

    static List<Direction> reflectBeam(Direction dir, char c) {
        return switch (c) {
            case '.' -> List.of(dir);
            case '/' -> List.of(
                    switch (dir) {
                        case U -> Direction.R;
                        case L -> Direction.D;
                        case D -> Direction.L;
                        case R -> Direction.U;
                    }
            );
            case '\\' -> List.of(
                    switch (dir) {
                        case U -> Direction.L;
                        case L -> Direction.U;
                        case D -> Direction.R;
                        case R -> Direction.D;
                    }
            );
            case '-' -> (dir == Direction.U || dir == Direction.D)
                    ? List.of(Direction.L, Direction.R)
                    : List.of(dir);
            case '|' -> (dir == Direction.L || dir == Direction.R)
                    ? List.of(Direction.U, Direction.D)
                    : List.of(dir);
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    private static long getNumEnergized(CharFieldLinear field, Beam initialBeam) {
        final int[] cellStates = new int[field.field.length];

        final Queue<Beam> queue = new ArrayDeque<>();
        queue.add(initialBeam);

        long numEnergizedCells = 0;

        while (!queue.isEmpty()) {
            final Beam beam = queue.poll();
            int nextCell = field.getNextCellIdxWithinField(beam.cell, beam.dir);
            if (nextCell == -1) {
                continue;
            }
            char c = field.get(nextCell);
            List<Direction> reflected = reflectBeam(beam.dir, c);
            for (Direction nextDir : reflected) {
                int mask = 1 << nextDir.ordinal();
                int oldState = cellStates[nextCell];
                boolean notAlreadyPresent = (oldState & mask) == 0;
                if (notAlreadyPresent) {
                    if (oldState == 0) {
                        numEnergizedCells++;
                    }
                    cellStates[nextCell] |= mask;
                    queue.add(new Beam(nextCell, nextDir));
                }
            }
        }
        return numEnergizedCells;
    }

    public static TaskSolution solve(String input) {
        CharFieldLinear field = CharFieldLinear.of(input);

        long result1 = getNumEnergized(field, new Beam(-1, Direction.R));

        long result2 = 0;
        int stride = field.stride;
        int idxOutsideL = -1;
        int idxOutsideR = stride;
        for (int row = 0; row < field.getNumRows(); row++) {
            result2 = Math.max(result2, getNumEnergized(field, new Beam(idxOutsideL, Direction.R)));
            result2 = Math.max(result2, getNumEnergized(field, new Beam(idxOutsideR, Direction.L)));
            idxOutsideL += stride;
            idxOutsideR += stride;
        }
        int idxOutsideU = -stride;
        int idxOutsideD = field.field.length + 1;
        for (int col = 0; col < field.getNumCols(); col++) {
            result2 = Math.max(result2, getNumEnergized(field, new Beam(idxOutsideU, Direction.D)));
            result2 = Math.max(result2, getNumEnergized(field, new Beam(idxOutsideD, Direction.U)));
            idxOutsideU++;
            idxOutsideD++;
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_16::solve, false);
        Helpers.runTask(MAIN, Day_16::solve, false);
    }
}

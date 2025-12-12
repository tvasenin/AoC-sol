package aoc2023;

import util.CharFieldLinear;
import util.Direction;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Day_16 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2023/16-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2023/16-main.txt");


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

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        CharFieldLinear field = CharFieldLinear.of(INPUT);

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

        System.out.printf("Time: %d ms\n", Duration.between(start, Instant.now()).toMillis());

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 8112) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 8314) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

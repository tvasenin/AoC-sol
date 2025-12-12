package aoc2023;

import util.Cell;
import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Day_21_Cheat {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/21-test.txt"), 64, -1);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/21-main.txt"), 3776, 625587097150084L);


    private static final TaskData EMPTY = new TaskData("...\n.S.\n...", -1, -1);


    private static Set<Cell> simulateStep(CharField field, Set<Cell> startCells) {
        int numRows = field.numRows;
        int numCols = field.numCols;
        Set<Cell> newCells = new HashSet<>();
        for (Cell cell : startCells) {
            List<Cell> filteredNeighbors = Helpers.getValidNeighborsStream(cell, numRows, numCols, true)
                    .filter(c -> field.get(c) != '#').toList();
            newCells.addAll(filteredNeighbors);
        }
        return newCells;
    }

    private static Set<Cell> simulateWithPaint(CharField field, Cell startCell, int numSteps) {
        Set<Cell> cells = Set.of(startCell);
        for (int i = 0; i < numSteps; i++) {
            cells = simulateStep(field, cells);
            cells.forEach(cell -> field.set(cell, 'O'));
        }
        return cells;
    }

    private static void countTileRegionValues(CharField megaField, int scale,
                                              AtomicInteger cntEvenTile, AtomicInteger cntOddTile,
                                              AtomicInteger cntEvenCorners, AtomicInteger cntOddCorners
    ) {
        if (megaField.numRows != megaField.numCols) {
            throw new IllegalArgumentException("Non-square megaField!");
        }
        int len = megaField.numRows / scale;
        int startI = (scale/2)*len;
        int startJ = (scale/2)*len;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                int realI = startI + i;
                int realJ = startJ + j;
                if (megaField.get(realI, realJ) == 'O') {
                    boolean isCorners = Math.min(i, (len - 1 - i)) + Math.min(j, len - 1 - j) < len / 2;
                    boolean isOdd = (i + j) % 2 == 0; // !!!
                    if (isOdd) {
                        cntOddTile.incrementAndGet();
                        if (isCorners) {
                            cntOddCorners.incrementAndGet();
                        }
                    } else {
                        cntEvenTile.incrementAndGet();
                        if (isCorners) {
                            cntEvenCorners.incrementAndGet();
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unused", "DuplicatedCode"})
    private static long countMegaResult(CharField megaField, int scale, int numMegaSteps) {
        // Original algorithm
        if (megaField.numRows != megaField.numCols) {
            throw new IllegalArgumentException("Non-square megaField!");
        }
        int len = megaField.numRows / scale;

        AtomicInteger cntEvenTileA = new AtomicInteger(0), cntOddTileA = new AtomicInteger(0);
        AtomicInteger cntEvenCornersA = new AtomicInteger(0), cntOddCornersA = new AtomicInteger(0);

        countTileRegionValues(megaField, scale, cntEvenTileA, cntOddTileA, cntEvenCornersA, cntOddCornersA);

        int cntEvenTile = cntEvenTileA.get(), cntOddTile = cntOddTileA.get();
        int cntEvenCorners = cntEvenCornersA.get(), cntOddCorners = cntOddCornersA.get();

        int cntEvenCenter = cntEvenTile - cntEvenCorners;
        int cntOddCenter = cntOddTile - cntOddCorners;

        int width = numMegaSteps * 2 + 1;
        if (width % (3 * len) != 0) {
            throw new IllegalArgumentException();
        }

        int widthInTiles = width / len;
        long radiusWithoutCenter = (widthInTiles - 1) / 2;
        boolean isOddCorners = radiusWithoutCenter % 2 != 0;
        int sumCorners = isOddCorners
                ? 4 * cntOddTile - 2 * cntOddCorners
                : 4 * cntEvenTile - 2 * cntEvenCorners;

        final long cntInternalEven, cntInternalOdd;
        if (isOddCorners) {
            cntInternalEven = radiusWithoutCenter * radiusWithoutCenter;
            cntInternalOdd = (radiusWithoutCenter-1) * (radiusWithoutCenter-1);
        } else {
            cntInternalOdd = radiusWithoutCenter * radiusWithoutCenter;
            cntInternalEven = (radiusWithoutCenter-1) * (radiusWithoutCenter-1);
        }

        long sumInternal = cntInternalEven * cntEvenTile + cntInternalOdd * cntOddTile;

        long sumEdgesQuarter = isOddCorners
                ? cntEvenTile - cntEvenCenter
                : cntOddTile - cntOddCenter;
        sumEdgesQuarter *= radiusWithoutCenter;

        long sumEdgesWithoutQuarter = isOddCorners
                ? cntOddTile * 4L - (cntOddTile - cntOddCenter)
                : cntEvenTile * 4L - (cntEvenTile - cntEvenCenter);
        sumEdgesWithoutQuarter *= (radiusWithoutCenter - 1);

        long sumEdges = sumEdgesQuarter + sumEdgesWithoutQuarter;

        return sumInternal + sumEdges + sumCorners;
    }

    @SuppressWarnings("DuplicatedCode")
    private static long countMegaResultOptimized(CharField megaField, int scale, int numMegaSteps) {
        // Optimized inlined algorithm
        if (megaField.numRows != megaField.numCols) {
            throw new IllegalArgumentException("Non-square megaField!");
        }
        int len = megaField.numRows / scale;

        AtomicInteger cntEvenTileA = new AtomicInteger(0), cntOddTileA = new AtomicInteger(0);
        AtomicInteger cntEvenCornersA = new AtomicInteger(0), cntOddCornersA = new AtomicInteger(0);

        countTileRegionValues(megaField, scale, cntEvenTileA, cntOddTileA, cntEvenCornersA, cntOddCornersA);

        int width = numMegaSteps * 2 + 1;
        if (width  % (3 * len) != 0) {
            throw new IllegalArgumentException();
        }

        int widthInTiles = width / len;
        long radiusWithoutCenter = (widthInTiles - 1) / 2;
        boolean isOddCorners = radiusWithoutCenter % 2 != 0;

        long numEvenTiles = isOddCorners ? radiusWithoutCenter * radiusWithoutCenter : (radiusWithoutCenter + 1) * (radiusWithoutCenter + 1);
        long numOddTiles = isOddCorners ? (radiusWithoutCenter+1) * (radiusWithoutCenter+1) : radiusWithoutCenter * radiusWithoutCenter;
        long numEvenCorners = isOddCorners ? radiusWithoutCenter : -(radiusWithoutCenter + 1);
        long numOddCorners = isOddCorners ? -(radiusWithoutCenter + 1) : radiusWithoutCenter;

        return numEvenTiles * cntEvenTileA.get() + numOddTiles * cntOddTileA.get()
                + numEvenCorners * cntEvenCornersA.get() + numOddCorners * cntOddCornersA.get();
    }

    public static TaskSolution solve(String input) {
        CharField field = CharField.of(input);
        int numRows = field.numRows;
        int numCols = field.numRows;

        Cell startCell = field.findFirstCell('S').orElseThrow();
        long result1 = simulateWithPaint(field, startCell, 64).size();

        // use AtomicBoolean to evade IDE ConstantValue warnings
        AtomicBoolean debug = new AtomicBoolean(false);

        char[][] innerField = field.field;
        int scale = debug.get() ? 3 : 1;
        int numMegaRows = numRows * scale;
        int numMegaCols = numCols * scale;
        CharField megaField = new CharField(numMegaRows, numMegaCols, (char) 0);
        // TODO: Arrays.copy()
        for (int i = 0; i < numMegaRows; i++) {
            for (int j = 0; j < numMegaCols; j++) {
                megaField.set(i, j, innerField[i % numRows][j % numCols]);
            }
        }

        // FIXME: Cheat
        int numSteps2 = 26501365;
//        int numSteps2 = 13;
        int minStepsToFillSingleMap = 130;
        int cycleOffset = debug.get() ? (numCols / 2) + numCols : minStepsToFillSingleMap;

        Cell startMegaCell = new Cell(startCell.row() + (scale/2)*numRows, startCell.col() + (scale/2)*numCols);

        // Paint the whole mega-tile (in order to fully paint the center tile)
        simulateWithPaint(megaField, startMegaCell, cycleOffset);
        if (debug.get()) {
            Helpers.printCharField(megaField);
        }

        long result2 = countMegaResultOptimized(megaField, scale, numSteps2);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        // FIXME
//        Helpers.runTask(EMPTY, Day_21_Cheat::solve, false);
//        Helpers.runTask(TEST, Day_21_Cheat::solve, false);
        Helpers.runTask(MAIN, Day_21_Cheat::solve, false);
    }
}

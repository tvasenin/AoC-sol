package aoc2024;

import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import util.CharFieldLinear;
import util.Direction;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Day_12 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/12-test.txt"), 1930, 1206);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/12-main.txt"), 1400386, 851994);


    private record AreaInfo(int area, int perimeter, int numSides) { }

    private static AreaInfo processConnectedComponent(CharFieldLinear field, int startCell) {
        Map<Direction, MutableIntList> borderClasses = new HashMap<>();
        BitSet cells = new BitSet(field.field.length);
        int perimeter = 0;
        char c = field.get(startCell);
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(startCell);
        while (!queue.isEmpty()) {
            int curCell = queue.poll();
            cells.set(curCell);
            for (Direction direction : Direction.values()) {
                int nextCell = field.getNextCellIdxNoCheck(curCell, direction);
                if (!cells.get(nextCell)) {
                    if (field.get(nextCell) == c) {
                        cells.set(nextCell);
                        queue.add(nextCell);
                    } else {
                        borderClasses.computeIfAbsent(direction, k -> IntLists.mutable.empty()).add(curCell);
                        perimeter++;
                    }
                }
            }
            // Erase processed cell
            field.set(curCell, (char) 0);
        }
        int area = cells.cardinality();
        int numSides = getNumSides(borderClasses, field.stride);
        return new AreaInfo(area, perimeter, numSides);
    }

    private static int getNumSides(Map<Direction, MutableIntList> borderClasses, int stride) {
        // Transpose vertical borders to become horizontal
        borderClasses.computeIfPresent(Direction.L, (dir, borders) -> transposeCells(borders, stride));
        borderClasses.computeIfPresent(Direction.R, (dir, borders) -> transposeCells(borders, stride));

        AtomicInteger numSides = new AtomicInteger();
        for (MutableIntList borders : borderClasses.values()) {
            // Count continuous index intervals
            borders.sortThis();
            AtomicInteger idxRef = new AtomicInteger(-1);
            borders.forEach(idx -> {
                if (idxRef.get() != idx) {
                    numSides.incrementAndGet();
                }
                idxRef.set(idx + 1);
            });
        }
        return numSides.get();
    }

    private static MutableIntList transposeCells(MutableIntList borders, int stride) {
        for (int i = 0; i < borders.size(); i++) {
            int idx = borders.get(i);
            int row = idx / stride;
            int col = idx % stride;
            int idxTransposed = col * stride + row;
            borders.set(i, idxTransposed);
        }
        return borders;
    }

    private static void getFencePrices(CharFieldLinear origField, AtomicLong price1, AtomicLong price2) {
        CharFieldLinear field = origField.pad(1, (char) 0);
        for (int cell = 0; cell < field.field.length; cell++) {
            if (field.get(cell) == 0) {
                continue;
            }
            AreaInfo areaInfo = processConnectedComponent(field, cell);
            price1.addAndGet((long) areaInfo.perimeter * areaInfo.area);
            price2.addAndGet((long) areaInfo.numSides * areaInfo.area);
        }
    }

    public static TaskSolution solve(String input) {
        CharFieldLinear origField = CharFieldLinear.of(input);
        AtomicLong price1 = new AtomicLong();
        AtomicLong price2 = new AtomicLong();
        getFencePrices(origField, price1, price2);
        long result1 = price1.get();
        long result2 = price2.get();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        IntLists.mutable.empty(); // Warmup
        Helpers.runTask(TEST, Day_12::solve, false);
        Helpers.runTask(MAIN, Day_12::solve, false);
    }
}

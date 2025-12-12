package aoc2023;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day_22 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/22-test.txt"), 5, 7);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/22-main.txt"), 495, 76158);


    private record Brick(Range<Integer> rangeX, Range<Integer> rangeY, Range<Integer> rangeZ) { }

    private static boolean isOverlapXY(Brick brick1, Brick brick2) {
        Range<Integer> rangeX1 = brick1.rangeX();
        Range<Integer> rangeY1 = brick1.rangeY();
        Range<Integer> rangeX2 = brick2.rangeX();
        Range<Integer> rangeY2 = brick2.rangeY();
        return rangeX1.isOverlappedBy(rangeX2) && rangeY1.isOverlappedBy(rangeY2);
    }

    private static List<Brick> dropBricksWithSorting(List<Brick> bricks) {
        List<Brick> droppedBricks = new ArrayList<>();
        bricks.stream().sorted(Comparator.comparingInt(brick -> brick.rangeZ().getMinimum())).forEach(brick -> {
            int minZ = 1;
            for (Brick droppedBrick : droppedBricks) {
                if (isOverlapXY(brick, droppedBrick)) {
                    minZ = Math.max(minZ, droppedBrick.rangeZ().getMaximum() + 1);
                }
            }
            int shiftZ = brick.rangeZ().getMinimum() - minZ;
            Range<Integer> newRangeZ = Range.of(minZ, brick.rangeZ().getMaximum() - shiftZ);
            droppedBricks.add(new Brick(brick.rangeX(), brick.rangeY(), newRangeZ));
        });
        return droppedBricks;
    }

    private static Map<Brick, Set<Brick>> getSupportersMap(List<Brick> sortedDroppedBricks) {
        Map<Brick, Set<Brick>> supportedBy = new HashMap<>();
        for (Brick brickA : sortedDroppedBricks) {
            int maxZ = brickA.rangeZ().getMaximum();
            for (Brick brickB : sortedDroppedBricks) {
                if (brickB.rangeZ().getMinimum() == maxZ + 1 && isOverlapXY(brickA, brickB)) {
                    supportedBy.computeIfAbsent(brickB, k -> new HashSet<>()).add(brickA);
                }
            }
        }
        return supportedBy;
    }

    private static Set<Brick> getFallenBricks(List<Brick> droppedSortedBricks, Brick removedBrick,
                                              Map<Brick, Set<Brick>> supportersMap
    ) {
        Set<Brick> fallenBricks = new HashSet<>();
        fallenBricks.add(removedBrick);
        for (Brick brick : droppedSortedBricks) {
            Set<Brick> supporters = supportersMap.getOrDefault(brick, Collections.emptySet());
            if (!supporters.isEmpty() && fallenBricks.containsAll(supporters)) {
                fallenBricks.add(brick);
            }
        }
        fallenBricks.remove(removedBrick);
        return fallenBricks;
    }

    public static TaskSolution solve(String input) {
        List<Brick> origBricks = new ArrayList<>();

        input.lines().forEach(line -> {
            int[] c = Arrays.stream(StringUtils.split(line, ",~")).mapToInt(Integer::parseInt).toArray();
            origBricks.add(new Brick(Range.of(c[0], c[3]), Range.of(c[1], c[4]), Range.of(c[2], c[5])));
        });

        List<Brick> droppedSortedBricks = dropBricksWithSorting(origBricks);

        Map<Brick, Set<Brick>> supportedBy = getSupportersMap(droppedSortedBricks);

        int[] numFallenBricks = droppedSortedBricks.stream()
                .mapToInt(removedBrick -> getFallenBricks(droppedSortedBricks, removedBrick, supportedBy).size())
                .toArray();

        long result1 = Arrays.stream(numFallenBricks).filter(v -> v == 0).count();
        long result2 = Arrays.stream(numFallenBricks).sum();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_22::solve, false);
        Helpers.runTask(MAIN, Day_22::solve, false);
    }
}

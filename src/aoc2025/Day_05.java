package aoc2025;

import org.apache.commons.lang3.LongRange;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Day_05 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/05-test.txt"), 3, 14);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/05-main.txt"), 735, 344306344403172L);


    private static List<LongRange> mergeSortRanges(List<LongRange> ranges) {
        List<LongRange> result = new ArrayList<>(ranges);
        result.sort(Comparator.comparingLong(LongRange::getMinimum).thenComparingLong(LongRange::getMaximum));
        int i = 0;
        while (i < result.size() - 1) {
            LongRange rangeCurr = result.get(i);
            Iterator<LongRange> it = result.listIterator(i + 1);
            while (it.hasNext()) {
                LongRange rangeTest = it.next();
                if (rangeTest.isAfterRange(rangeCurr)) {
                    break;
                }
                if (rangeTest.getMaximum() > rangeCurr.getMaximum()) {
                    // Merge ranges and replace
                    rangeCurr = LongRange.of(rangeCurr.getMinimum(), rangeTest.getMaximum());
                    result.set(i, rangeCurr);
                }
                it.remove();
            }
            i++;
        }
        return result;
    }

    public static TaskSolution solve(String input) {
        String[] s0 = input.split("\n\n");

        List<LongRange> rangesOrig = s0[0].lines()
                .map(str -> {
                    String[] s00 = str.split("-");
                    return LongRange.of(Long.parseUnsignedLong(s00[0]), Long.parseUnsignedLong(s00[1]));
                })
                .toList();

        long[] ids = s0[1].lines()
                .mapToLong(Long::parseUnsignedLong)
                .toArray();

        List<LongRange> rangesMergedSorted = mergeSortRanges(rangesOrig);

        long result1 = 0;
        for (long id : ids) {
            for (LongRange range : rangesMergedSorted) {
                if (range.contains(id)) {
                    result1++;
                    break;
                }
            }
        }

        long result2 = rangesMergedSorted.stream().mapToLong(r -> r.getMaximum() - r.getMinimum() + 1).sum();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_05::solve, true);
        Helpers.runTask(MAIN, Day_05::solve, true);
    }
}

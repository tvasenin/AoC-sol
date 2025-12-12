package aoc2024;

import org.eclipse.collections.api.block.comparator.primitive.IntComparator;
import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.factory.primitive.IntSets;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import util.CollectionUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_05_Cheat {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/05-test.txt"), 143, 123);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/05-main.txt"), 5275, 6191);


    @SuppressWarnings("unused")
    private static boolean isOk(IntList pages, IntObjectMap<MutableIntSet> rules) {
        for (int i = 1; i < pages.size(); i++) {
            int page = pages.get(i);
            IntSet pagesNotBefore = rules.getIfAbsent(page, IntSets.mutable::empty);
            if (pagesNotBefore.isEmpty()) {
                continue;
            }
            for (int j = 0; j < i; j++) {
                if (pagesNotBefore.contains(pages.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static IntComparator getPageOrderComparator(IntObjectMap<MutableIntSet> rules) {
        return (p1, p2) -> {
            if (p1 == p2) {
                return 0;
            }
            boolean isOk1 = rules.getIfAbsent(p1, IntSets.mutable::empty).contains(p2);
            boolean isOk2 = rules.getIfAbsent(p2, IntSets.mutable::empty).contains(p1);
            if (isOk1 && isOk2) {
                throw new IllegalArgumentException(String.format("Both %s|%s and %s|%s rules found", p1, p2, p2, p1));
            }
            if (!isOk1 && !isOk2) {
                throw new IllegalArgumentException(String.format("Neither %s|%s or %s|%s rule found", p1, p2, p2, p1));
            }
            return isOk1 ? -1 : 1;
        };
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;

        MutableIntObjectMap<MutableIntSet> rules = IntObjectMaps.mutable.empty();

        String[] sections = input.split("\n\n");

        for (String line : sections[0].split("\n")) {
            String[] s0 = line.split("\\|");
            int pageBefore = Integer.parseUnsignedInt(s0[0]);
            int pageAfter = Integer.parseUnsignedInt(s0[1]);
            rules.getIfAbsentPut(pageBefore, IntSets.mutable.empty()).add(pageAfter);
        }

        for (String line : sections[1].split("\n")) {
            IntComparator comparator = getPageOrderComparator(rules);
            MutableIntList pages = CollectionUtils.parseToMutableIntList(line, ",");
            // This can be called cheating, as it's not guaranteed that the rules make full order for "correct"
            // page sequences, but in fact they do!
            // Uncomment the following line to perform the fully correct check:
//                    if (isOk(pages, rules)) {
            if (CollectionUtils.isSorted(pages, comparator)) {
                result1 += pages.get(pages.size() / 2);
            } else {
                // "Incorrect" sequences are explicitly guaranteed to be able to be unambiguously sorted
                pages.sortThis(comparator);
                result2 += pages.get(pages.size() / 2);
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        IntObjectMaps.mutable.empty(); // Warmup
        IntSets.mutable.empty(); // Warmup
        CollectionUtils.parseToMutableIntList("1 2", " "); // Warmup
        Helpers.runTask(TEST, Day_05_Cheat::solve, false);
        Helpers.runTask(MAIN, Day_05_Cheat::solve, false);
    }
}

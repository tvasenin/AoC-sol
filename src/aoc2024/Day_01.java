package aoc2024;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.factory.SortedBags;
import org.eclipse.collections.api.list.ImmutableList;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day_01 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/01-test.txt"), 11, 31);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/01-main.txt"), 1223326, 21070419);


    public static TaskSolution solve(String input) {
        List<Integer> list1 = new ArrayList<>();
        MutableSortedBag<Integer> bag2 = SortedBags.mutable.empty();
        long result1 = 0, result2 = 0;

        input.lines().forEach(line -> {
            String[] split = line.split("\\s+");
            list1.add(Integer.parseInt(split[0]));
            bag2.add(Integer.parseInt(split[1]));
        });
        Collections.sort(list1);
        ImmutableList<Integer> list2 = bag2.toImmutableSortedList();
        int n = list1.size();
        for (int i = 0; i < n; i++) {
            int first = list1.get(i);
            int second = list2.get(i);
            result1 += Math.abs(first - second);
            result2 += (long) first * bag2.occurrencesOf(first);
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        SortedBags.mutable.empty(); // Warmup
        Helpers.runTask(TEST, Day_01::solve, false);
        Helpers.runTask(MAIN, Day_01::solve, false);
    }
}

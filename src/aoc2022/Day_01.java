package aoc2022;

import org.eclipse.collections.api.block.comparator.primitive.IntComparator;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_01 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2022/01-test.txt"), 24000, 45000);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2022/01-main.txt"), 69693, 200945);


    public static TaskSolution solve(String input) {
        MutableIntList weights = IntLists.mutable.empty();
        int elfIdx = 0;
        for (String line : input.split("\n")) {
            if (line.isEmpty()) {
                elfIdx++;
            } else {
                if (elfIdx >= weights.size()) {
                    weights.add(0);
                }
                int oldWeight = weights.get(elfIdx);
                int weight = Integer.parseInt(line);
                weights.set(elfIdx, oldWeight + weight);
            }
        }
        IntComparator reverseIntComparator = (i1, i2) -> i2 - i1;
        MutableIntList top = weights.sortThis(reverseIntComparator);
        if (weights.size() < 3) {
            throw new RuntimeException("Too few elves: " + weights.size());
        }
        long result1 = top.get(0);
        long result2 = top.get(0) + top.get(1) + top.get(2);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        IntLists.mutable.empty(); // Warmup
        Helpers.runTask(TEST, Day_01::solve, true);
        Helpers.runTask(MAIN, Day_01::solve, true);
    }
}

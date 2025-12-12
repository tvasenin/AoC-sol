package aoc2024;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day_02 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/02-test.txt"), 2, 4);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/02-main.txt"), 598, 634);


    private static boolean isSafe(List<Integer> list) {
        if (list.isEmpty()) {
            throw new RuntimeException();
        }
        if (list.size() == 1) {
            return true;
        }
        boolean isIncreasing = list.get(2) > list.get(1);
        for (int i = 1; i < list.size(); i++) {
            int diff = list.get(i) - list.get(i - 1);
            int diffAbs = Math.abs(diff);
            if (diffAbs < 1 || diffAbs > 3) {
                return false;
            }
            if ((diff > 0) ^ isIncreasing) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSafeWithSingleSkip(List<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            List<Integer> list2 = new ArrayList<>(list);
            //noinspection SuspiciousListRemoveInLoop
            list2.remove(i);
            if (isSafe(list2)) {
                return true;
            }
        }
        return false;
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            List<Integer> list = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).boxed().toList();
            if (isSafe(list)) {
                result1++;
                result2++;
            } else {
                if (isSafeWithSingleSkip(list)) {
                    result2++;
                }
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_02::solve, true);
        Helpers.runTask(MAIN, Day_02::solve, true);
    }
}

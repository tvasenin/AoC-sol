package aoc2017;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_01 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2017/01-main.txt"), 1158, 1132);


    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        int n = input.length();
        if (n % 2 != 0) {
            throw new IllegalArgumentException("Input must be even");
        }
        for (int i = 0; i < n; i++) {
            char curr = input.charAt(i);
            char next = input.charAt((i + 1) % n);
            char nextHalfway = input.charAt((i + (n / 2)) % n);
            if (curr == next) {
                if (!Character.isDigit(curr)) {
                    throw new IllegalStateException("Unexpected value: " + curr);
                }
                result1 += Character.getNumericValue(curr);
            }
            if (curr == nextHalfway) {
                if (!Character.isDigit(curr)) {
                    throw new IllegalStateException("Unexpected value: " + curr);
                }
                result2 += Character.getNumericValue(curr);
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(MAIN, Day_01::solve, true);
    }
}

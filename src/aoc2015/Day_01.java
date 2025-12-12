package aoc2015;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_01 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2015/01-main.txt"), 138, 1771);


    public static TaskSolution solve(String input) {
        long result1 = 0;
        long result2 = -1;
        int curStep = 0;

        for (char c : input.toCharArray()) {
            curStep++;
            switch (c) {
                case '(' -> result1++;
                case ')' -> result1--;
                default -> throw new IllegalStateException("Unexpected value: " + c);
            }
            if (result1 < 0 && result2 == -1) {
                result2 = curStep;
            }
        }
        if (result2 == -1) {
            throw new IllegalStateException("Unexpected: Santa never went down to the basement");
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(MAIN, Day_01::solve, true);
    }
}

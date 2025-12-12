package aoc2025;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_01 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/01-test.txt"), 3, 6);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/01-main.txt"), 980, 5961);


    private static int numClicks(int start, int count) {
        assert start >= 0;
        int n = Math.abs(count) / 100;
        int cMod = count % 100;
        if (start != 0) {
            int end = start + cMod;
            if (end >= 100 || end <= 0) {
                n++;
            }
        }
        return n;
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        int pos = 50;
        for (String line : input.split("\n")) {
            char sign = line.charAt(0) == 'L' ? '-' : '+';
            int count = Integer.parseInt(sign + line.substring(1));
            result2 += numClicks(pos, count);
            pos = Math.floorMod(pos + count, 100);
            result1 += pos == 0 ? 1 : 0;
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_01::solve, true);
        Helpers.runTask(MAIN, Day_01::solve, true);
    }
}

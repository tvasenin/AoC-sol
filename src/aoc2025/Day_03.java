package aoc2025;

import util.ArrayTools;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_03 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/03-test.txt"), 357, 3121910778619L);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/03-main.txt"), 17109, 169347417057382L);


    private static long getBestValue(int[] vals, int length) {
        return getBestValue(vals, length, 0, 0);
    }

    private static long getBestValue(int[] vals, int length, int idxFrom, long tempJoltage) {
        int lengthRemaining = length - 1;
        int idxTo = vals.length - lengthRemaining;
        int idx1 = ArrayTools.indexOfTheMaxByStream(vals, idxFrom, idxTo);
        tempJoltage += vals[idx1];
        if (lengthRemaining == 0) {
            return tempJoltage;
        }
        return getBestValue(vals, lengthRemaining, idx1 + 1, tempJoltage * 10);
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            int[] vals = line.chars().map(c -> c - '0').toArray();
            result1 += getBestValue(vals, 2);
            result2 += getBestValue(vals, 12);
        }
        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_03::solve, true);
        Helpers.runTask(MAIN, Day_03::solve, true);
    }
}

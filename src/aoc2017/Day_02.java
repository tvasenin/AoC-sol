package aoc2017;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;

public class Day_02 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2017/02-main.txt"), 39126, 258);


    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            int[] values = Helpers.parseIntArray(line, "\\s+");
            Arrays.sort(values);
            result1 += values[values.length - 1] - values[0];
            boolean found = false;
            // Scan sorted array for an evenly divisible pair
            for (int i = 0; i < values.length - 1; i++) {
                for (int j = i + 1; j < values.length; j++) {
                    int divResult = values[j] / values[i];
                    int modResult = values[j] % values[i];
                    if (modResult == 0) {
                        found = true;
                        result2 += divResult;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(MAIN, Day_02::solve, true);
    }
}

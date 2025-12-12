package aoc2023;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;

public class Day_09 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/09-test.txt"), 114, 2);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/09-main.txt"), 1684566095, 1136);


    static int[] genDiff(int[] input) {
        int[] result = new int[input.length-1];
        for (int i = 0; i < input.length - 1; i++) {
            result[i] = input[i+1] - input[i];
        }
        return result;
    }

    public static TaskSolution solve(String input) {
        long result1 = 0;
        long result2 = 0;
        for (String line : input.split("\n")) {
            boolean plus = true;
            int[] numbers = Helpers.parseIntArray(line, " ");
            while (!Arrays.stream(numbers).allMatch(e -> e == 0)) {
                result1 += numbers[numbers.length - 1];
                result2 += numbers[0] * (plus ? 1 : -1);
                numbers = genDiff(numbers);
                plus = !plus;
            }
        }
        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_09::solve, true);
        Helpers.runTask(MAIN, Day_09::solve, true);
    }
}

package aoc2025;

import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day_06 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/06-test.txt"), 4277556, 3263827);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/06-main.txt"), 6299564383938L, 11950004808442L);


    private static String transposeInput(String input) {
        CharField transposedField = CharField.of(input).transpose();
        return Arrays.stream(transposedField.field)
                .map(String::new)
                .collect(Collectors.joining("\n"));
    }

    public static TaskSolution solve(String input) {
        List<String> lines = input.lines().toList();

        char[] ops = lines.getLast().replace(" ", "").toCharArray();

        long[][] nums = lines.subList(0, lines.size() - 1).stream()
                .map(line -> Arrays.stream(line.trim().split("\\s+")).mapToLong(Long::parseLong).toArray())
                .toArray(long[][]::new);

        assert nums[0].length == ops.length;
        long[] res1 = Arrays.copyOf(nums[0], ops.length);

        for (int r = 1; r < nums.length; r++) {
            long[] row = nums[r];
            assert row.length == ops.length;
            for (int c = 0; c < ops.length; c++) {
                if (ops[c] == '*') {
                    res1[c] *= row[c];
                } else {
                    res1[c] += row[c];
                }
            }
        }

        long result1 = Arrays.stream(res1).sum();

        String lines2 = transposeInput(input);

        String[] groups2 =  lines2.replace('*', ' ').replace('+', ' ').split("\n\\s+\n");

        long[] res2 = new long[ops.length];

        for (int i = 0; i < groups2.length; i++) {
            long[] groupNums = Arrays.stream(groups2[i].trim().split("\n"))
                    .map(String::trim)
                    .mapToLong(Long::parseLong)
                    .toArray();
            if (ops[i] == '*') {
                long mul = 1;
                for (long groupNum : groupNums) {
                    mul *= groupNum;
                }
                res2[i] = mul;
            } else {
                res2[i] = Arrays.stream(groupNums).sum();
            }
        }

        long result2 = Arrays.stream(res2).sum();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_06::solve, false);
        Helpers.runTask(MAIN, Day_06::solve, false);
    }
}

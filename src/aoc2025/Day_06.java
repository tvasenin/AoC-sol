package aoc2025;

import util.CharField;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day_06 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2025/06-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2025/06-main.txt");

    private static String transposeInput(String input) {
        CharField transposedField = CharField.of(input).transpose();
        return Arrays.stream(transposedField.field)
                .map(String::new)
                .collect(Collectors.joining("\n"));
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

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

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 6299564383938L) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 11950004808442L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

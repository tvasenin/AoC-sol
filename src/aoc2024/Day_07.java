package aoc2024;

import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Day_07 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/07-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/07-main.txt");

    private static long append(long prefix, long suffix) {
        long temp = 10;
        while (temp <= suffix) temp *= 10;
        return prefix * temp + suffix;
    }

    private static boolean canCombine1Rec(long[] data, long target) {
        if (data.length == 0) throw new IllegalArgumentException();
        return canCombine1RecImpl(data, target, 1, data[0]);
    }

    private static boolean canCombine1RecImpl(long[] data, long target, int idx, long intermediate) {
        if (idx >= data.length) return intermediate == target;
        if (intermediate > target) return false;
        long num = data[idx++];
        // Order of operations was tuned for performance for the current input :)
        return canCombine1RecImpl(data, target, idx, intermediate * num)
               || canCombine1RecImpl(data, target, idx, intermediate + num);
    }

    private static boolean canCombine2Rec(long[] data, long target) {
        if (data.length == 0) throw new IllegalArgumentException();
        return canCombine2RecImpl(data, target, 1, data[0]);
    }

    private static boolean canCombine2RecImpl(long[] data, long target, int idx, long intermediate) {
        if (idx >= data.length) return intermediate == target;
        if (intermediate > target) return false;
        long num = data[idx++];
        // Order of operations was tuned for performance for the current input :)
        return canCombine2RecImpl(data, target, idx, append(intermediate, num))
               || canCombine2RecImpl(data, target, idx, intermediate * num)
               || canCombine2RecImpl(data, target, idx, intermediate + num);
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            String[] s0 = line.split(": ");
            long target = Long.parseLong(s0[0]);
            long[] data = Arrays.stream(s0[1].split(" ")).mapToLong(Long::parseLong).toArray();
            if (canCombine1Rec(data, target)) {
                result1 += target;
                result2 += target;
            } else {
                if (canCombine2Rec(data, target)) {
                    result2 += target;
                }
            }
        }

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 1038838357795L) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 254136560217241L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

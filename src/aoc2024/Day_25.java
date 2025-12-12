package aoc2024;

import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Day_25 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/25-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/25-main.txt");


    private static boolean isMatch(int[] lock, int[] key) {
        for (int i = 0; i < 5; i++) {
            if (lock[i] + key[i] > 5) {
                return false;
            }
        }
        return true;
    }

    private static long getNumMatchingPairs(List<int[]> locks, List<int[]> keys) {
        long numMatching = 0;
        for (int[] lock : locks) {
            for (int[] key : keys) {
                if (isMatch(lock, key)) {
                    numMatching++;
                }
            }
        }
        return numMatching;
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        String[] s0 = input.split("\n\n");

        List<int[]> locks = new ArrayList<>();
        List<int[]> keys = new ArrayList<>();

        for (String shapeStr : s0) {
            int[] shape = new int[5];
            String[] rows = shapeStr.split("\n");
            if (rows.length != 7) {
                throw new IllegalArgumentException("Invalid shape: " + shapeStr);
            }
            for (int i = 1; i < rows.length - 1; i++) {
                String row = rows[i];
                for (int j = 0; j < row.length(); j++) {
                    char ch = row.charAt(j);
                    if (ch == '#') {
                        shape[j]++;
                    }
                }
            }
            boolean isLock = shapeStr.charAt(0) == '#';
            if (isLock) {
                locks.add(shape);
            } else {
                keys.add(shape);
            }
        }

        long result1 = getNumMatchingPairs(locks, keys);

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        if (!isTest) {
            if (result1 != 3338) {
                System.out.println("Wrong Result 1");
            }
        }
        System.out.println("Result (Part 1): " + result1);
    }

}

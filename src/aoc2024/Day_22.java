package aoc2024;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.BitSet;

public class Day_22 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/22-main.txt"), 18694566361L, 2100);


    private static final int MODULO = 1 << 24;

    public static long getNextArray(long num, int[] output) {
        output[0] = (int) (num % 10);
        for (int i = 0; i < output.length - 1; i++) {
            num = getNext(num);
            // Save last digit
            output[i + 1] = (int) (num % 10);
        }
        return num;
    }

    public static long getNext(long num) {
        num = num ^ (num << 6);
        num = num % MODULO;
        num = num ^ num >> 5;
//        num = num % MODULO;
        num = num ^ num << 11;
        num = num % MODULO;
        return num;
    }

    private static int getPatternNumber(int base, int p0, int p1, int p2, int p3, int p4) {
        // Translate pattern to unique key
        int pattern = (p1 - p0) + 9;
        pattern = pattern * base + ((p2 - p1) + 9);
        pattern = pattern * base + ((p3 - p2) + 9);
        pattern = pattern * base + ((p4 - p3) + 9);
        return pattern;
    }

    public static TaskSolution solve(String input) {
        long result1 = 0;

        int[] numbers = new int[2001];

        int base = 9 - (-9) + 1;
        int numDiffCombinations = base * base * base * base;
        int[] totals = new int[numDiffCombinations];
        int maxValue = 0;
        BitSet seenPatterns = new BitSet(numDiffCombinations);

        for (String line : input.split("\n")) {
            long numStart = Long.parseUnsignedLong(line);
            long lastNumber = getNextArray(numStart, numbers);
            result1 += lastNumber;

            seenPatterns.clear();
            int p0 = numbers[0], p1 = numbers[1], p2 = numbers[2], p3 = numbers[3];
            for (int j = 4; j < numbers.length; j++) {
                int p4 = numbers[j];
                int pattern = getPatternNumber(base, p0, p1, p2, p3, p4);
                if (!seenPatterns.get(pattern)) {
                    seenPatterns.set(pattern);
                    int newValue = totals[pattern] + p4;
                    if (newValue > maxValue) {
                        maxValue = newValue;
                    }
                    totals[pattern] = newValue;
                }
                p0 = p1;
                p1 = p2;
                p2 = p3;
                p3 = p4;
            }
        }

        long result2 = maxValue;

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(MAIN, Day_22::solve);
    }
}

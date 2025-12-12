package aoc2023;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Day_12 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/12-test.txt"), 21, 525152);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/12-main.txt"), 7792, 13012052341533L);


    static final Map<IntBuffer, Long> MEMO = new HashMap<>();

    private static long getNumCombinations(char[] field, int[] groupLengths, IntBuffer key) {
        Long value = MEMO.get(key);
        if (value != null) {
            return value;
        }
        value = numValidCombinationsInner(field, groupLengths, key.get(0), key.get(1), key.get(2), key.get(3), key.get(4));
        MEMO.put(key, value);
        return value;
    }

    private static long numValidCombinationsInner(char[] field, int[] groupLengths, int pos, int burst, int groupIdx,
                                                  int remainingBroken, int remainingUnknown) {
        for (; pos < field.length; pos++) {
            char c = field[pos];
            switch (c) {
                case '?':
                    if (burst > 0) {
                        if (groupLengths[groupIdx] != burst) {
                            // should be broken
                            burst++;
                            remainingBroken--;
                        } else {
                            // should be working
                            burst = 0;
                        }
                    } else {
                        // Assume working if all broken parts were already matched
                        if (groupIdx != groupLengths.length - 1) {
                            // Can be either way, recursively try both
                            long numCombinations = 0;
                            // try broken
                            if (remainingBroken > 0) {
                                IntBuffer keyBroken = IntBuffer.wrap(new int[] { pos + 1, burst + 1, groupIdx + 1, remainingBroken - 1, remainingUnknown - 1 });
                                numCombinations += getNumCombinations(field, groupLengths, keyBroken);
                            }
                            // try working if there's budget
                            if (remainingBroken < remainingUnknown) {
                                IntBuffer keyWorking = IntBuffer.wrap(new int[] {pos + 1, burst, groupIdx, remainingBroken, remainingUnknown - 1 });
                                numCombinations += getNumCombinations(field, groupLengths, keyWorking);
                            }
                            return numCombinations;
                        }
                    }
                    remainingUnknown--;
                    break;
                case '#':
                    if (burst == 0) {
                        groupIdx++;
                        if (groupIdx >= groupLengths.length) {
                            return 0;
                        }
                    }
                    burst++;
                    break;
                case '.':
                    if (burst > 0) {
                        if (groupLengths[groupIdx] != burst) {
                            return 0;
                        }
                        burst = 0;
                    }
                    break;
                default: throw new IllegalArgumentException("Unexpected char!");
            }
        }
        if (groupIdx != groupLengths.length - 1) {
            return 0;
        }
        boolean isAbsentOrMatchingBurst = burst == 0 || groupLengths[groupIdx] == burst;
        return isAbsentOrMatchingBurst ? 1 : 0;
    }

    private static long numValidCombinations(char[] field, int[] groupLengths) {
        if (field.length > 128) {
            throw new IllegalArgumentException("Field too large for the memo keys!");
        }
        MEMO.clear();
        int totalBroken = Arrays.stream(groupLengths).sum();
        int remainingUnknown = 0;
        int numBroken = 0;
        for (char c : field) {
            switch (c) {
                case '?' -> remainingUnknown++;
                case '#' -> numBroken++;
            }
        }
        int remainingBroken = totalBroken - numBroken;
        return numValidCombinationsInner(field, groupLengths, 0, 0, -1, remainingBroken, remainingUnknown);
    }

    public static TaskSolution solve(String input) {

        int maxField = 0;

        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            String[] s1 = line.split(" ");
            char[] field1 = s1[0].toCharArray();
            int[] groupLengths1 = Helpers.parseIntArray(s1[1], ",");
            result1 += numValidCombinations(field1, groupLengths1);
            char[] field2 = String.join("?", s1[0], s1[0], s1[0], s1[0], s1[0]).toCharArray();
            int[] groupLengths2 = new int[groupLengths1.length * 5];
            for (int i = 0; i < groupLengths2.length; i++) {
                groupLengths2[i] = groupLengths1[i % groupLengths1.length];
            }
            long num2 = numValidCombinations(field2, groupLengths2);
            result2 += num2;
            maxField = Math.max(maxField, field2.length);
        }

        return TaskSolution.of(result1, result2);
    }


    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_12::solve, false);
        Helpers.runTask(MAIN, Day_12::solve, false);
    }
}

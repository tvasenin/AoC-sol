package aoc2023;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.List;

public class Day_01 {

    private static final TaskData TEST_1 = new TaskData(Resources.getResourceAsString("aoc2023/01-test-part1.txt"), 142, 142);
    private static final TaskData TEST_2 = new TaskData(Resources.getResourceAsString("aoc2023/01-test-part2.txt"), -1, 281);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/01-main.txt"), 54990, 54473);


    private static final List<String> SPELLED_DIGITS_WITHOUT_ZERO = List.of(
            "", // stub, should not be accessed
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine"
    );

    private static final int INDEX_NOT_FOUND = -1;

    private static boolean isDigitSimple(char c) {
        int val = c - '0';
        return val >= 0 && val <= 9;
    }

    private static int indexOfFirstCharDigit(CharSequence cs) {
        int csLength = cs.length();
        for (int idxFirst = 0; idxFirst < csLength; idxFirst++) {
            char c = cs.charAt(idxFirst);
            if (isDigitSimple(c)) {
                return idxFirst;
            }
        }
        return INDEX_NOT_FOUND;
    }

    private static int indexOfLastCharDigit(CharSequence cs) {
        int csLength = cs.length();
        for (int idxLast = csLength - 1; idxLast >= 0; idxLast--) {
            char c = cs.charAt(idxLast);
            if (isDigitSimple(c)) {
                return idxLast;
            }
        }
        return INDEX_NOT_FOUND;
    }

    private static int getCode1(CharSequence cs) {
        int firstDigit = cs.charAt(indexOfFirstCharDigit(cs)) - '0';
        int lastDigit = cs.charAt(indexOfLastCharDigit(cs)) - '0';
        return firstDigit * 10 + lastDigit;
    }

    private static int getFirstSpelledDigit(final String str) {
        int ret = str.length();
        int value = -1;
        // Start from 'one'
        for (int i = 1; i < SPELLED_DIGITS_WITHOUT_ZERO.size(); i++) {
            String search =  SPELLED_DIGITS_WITHOUT_ZERO.get(i);
            int tmp = str.indexOf(search);
            if (tmp == INDEX_NOT_FOUND) {
                continue;
            }
            if (tmp < ret) {
                ret = tmp;
                value = i; // position in list is the digit value
            }
        }
        return value;
    }

    private static int getLastSpelledDigit(final String str) {
        int ret = INDEX_NOT_FOUND;
        int value = -1;
        // Start from 'one'
        for (int i = 1; i < SPELLED_DIGITS_WITHOUT_ZERO.size(); i++) {
            String search =  SPELLED_DIGITS_WITHOUT_ZERO.get(i);
            int tmp = str.lastIndexOf(search);
            if (tmp > ret) {
                ret = tmp;
                value = i; // position in list is the digit value
            }
        }
        return value;
    }

    private static int getCode2(String s) {
        int firstCharDigitIdx = indexOfFirstCharDigit(s);
        final int firstDigit;
        final int lastDigit;
        if (firstCharDigitIdx == INDEX_NOT_FOUND) {
            // String has no digits, no need to check for the last digits
            firstDigit = getFirstSpelledDigit(s);
            lastDigit = getLastSpelledDigit(s);
        } else {
            // Both first and last digit indexes are found!
            int firstSpelledDigit = getFirstSpelledDigit(s.substring(0, firstCharDigitIdx));
            firstDigit = firstSpelledDigit != -1 ? firstSpelledDigit : s.charAt(firstCharDigitIdx) - '0';
            int lastCharDigitIdx = indexOfLastCharDigit(s);
            int lastSpelledDigit = getLastSpelledDigit(s.substring(lastCharDigitIdx + 1));
            lastDigit = lastSpelledDigit != -1 ? lastSpelledDigit : s.charAt(lastCharDigitIdx) - '0';
        }
        return firstDigit * 10 + lastDigit;
    }

    public static long solve1(String input) {
        return input.lines().mapToLong(Day_01::getCode1).sum();
    }

    public static long solve2(String input) {
        return input.lines().mapToLong(Day_01::getCode2).sum();
    }

    public static TaskSolution solve(String input) {
        long result1 = solve1(input);
        long result2 = solve2(input);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST_1, Day_01::solve, true);

        // Run part 2 only
        TaskSolution resultsTest2 = TaskSolution.of(-1, solve2(TEST_2.input()));
        Helpers.printResults(TEST_2, resultsTest2);

        Helpers.runTask(MAIN, Day_01::solve, true);
    }
}

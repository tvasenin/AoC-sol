package aoc2023;

import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Day_01 {

    private static final String INPUT_TEST_1 = Resources.getResourceAsString("aoc2023/01-test-part1.txt");
    private static final String INPUT_TEST_2 = Resources.getResourceAsString("aoc2023/01-test-part2.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2023/01-main.txt");


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

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input1 = isTest ? INPUT_TEST_1 : INPUT;
        //noinspection ConstantValue
        String input2 = isTest ? INPUT_TEST_2 : INPUT;

        Instant start = Instant.now();

        long result1 = input1.lines().mapToLong(Day_01::getCode1).sum();
        long result2 = input2.lines().mapToLong(Day_01::getCode2).sum();

        System.out.printf("Time: %d ms\n", Duration.between(start, Instant.now()).toMillis());

        //noinspection ConstantValue
        if (input1.equals(INPUT)) {
            if (result1 != 54990) {
                System.out.println("Wrong Result 1");
            }
        }
        //noinspection ConstantValue
        if (input2.equals(INPUT)) {
            if (result2 != 54473) {
                System.out.println("Wrong Result 2");
            }
        }

        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);

    }
}

package aoc2024;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import util.Resources;

import java.time.Duration;
import java.time.Instant;

public class Day_13_Cheat {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/13-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/13-main.txt");


    private static final long OFFSET = 10000000000000L;

    private static long det(long a, long b, long c, long d) {
        return a * d - b * c;
    }

    private static long minWinConsistent(long x, long y, int aX, int aY, int bX, int bY) {
        long det = det(aX, bX, aY, bY);
        long detX = det(x, bX, y, bY);
        long detY = det(aX, x, aY, y);
        if (det == 0) {
            if (detX != 0 || detY != 0) {
                // Inconsistent system - no solution
                return 0;
            }
            // TODO: Indeterminate system -- have to find integer optimum
            throw new NotImplementedException();
        }
        if ((detX % det != 0) || (detY % det != 0)) {
            // No integer solution
            return 0;
        }
        long numA = detX / det;
        long numB = detY / det;
        return numA * 3L + numB;
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        long result1 = 0, result2 = 0;
        for (String group : input.split("\n\n")) {
            String[] rules = group.split("\n");
            String[] ruleA = StringUtils.split(rules[0], "+,");
            String[] ruleB = StringUtils.split(rules[1], "+,");
            String[] ruleP = StringUtils.split(rules[2], "=,");
            long x = Integer.parseInt(ruleP[1]);
            long y = Integer.parseInt(ruleP[3]);
            int aX = Integer.parseInt(ruleA[1]);
            int aY = Integer.parseInt(ruleA[3]);
            int bX = Integer.parseInt(ruleB[1]);
            int bY = Integer.parseInt(ruleB[3]);
            result1 += minWinConsistent(x, y, aX, aY, bX, bY);
            result2 += minWinConsistent(x + OFFSET, y + OFFSET, aX, aY, bX, bY);
        }

        System.out.printf("Time: %d ms\n", Duration.between(start, Instant.now()).toMillis());

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 39996) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 73267584326867L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

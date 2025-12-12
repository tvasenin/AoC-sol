package aoc2025;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class Day_02 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/02-test.txt"), 1227775554L, 4174379265L);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/02-main.txt"), 40055209690L, 50857215650L);


    private static final long[] POWERS_OF_TEN10 = IntStream.range(0, 16)
            .mapToLong(i -> (long) Math.pow(10, i))
            .toArray();

    static long powerOfTen(int pow) {
        return POWERS_OF_TEN10[pow];
    }

    static int stringSize(long x) {
        int d = 1;
        if (x >= 0) {
            d = 0;
            x = -x;
        }
        long p = -10;
        for (int i = 1; i < 19; i++) {
            if (x > p)
                return i + d;
            p = 10 * p;
        }
        return 19 + d;
    }

    public static long sumInvalidIds1(long start, long end) {
        long sum = 0;
        for (long id = start; id <= end; id++) {
            int numDigits = stringSize(id);
            if (numDigits % 2 != 0) {
                id = powerOfTen(numDigits) - 1;
                continue;
            }

            long powHalf = powerOfTen(numDigits / 2);
            long a = id / powHalf;
            long b = id % powHalf;
            if (a == b) {
                sum += id;
            }
        }
        return sum;
    }

    public static boolean isInvalid(long x, int step) {
        long pow = powerOfTen(step);
        long part = x % pow;
        x = x / pow;
        while (x != 0) {
            if (x % pow != part) {
                return false;
            }
            x = x / pow;
        }
        return true;
    }

    public static long sumInvalidIds2(long start, long end) {
        Set<Long> invalidIds = new HashSet<>();
        for (long id = start; id <= end; id++) {
            int numDigits = stringSize(id);
            int numDigitsHalf = numDigits / 2;
            for (int step = 1; step <= numDigitsHalf; step++) {
                if (numDigits % step != 0) {
                    continue;
                }
                if (isInvalid(id, step)) {
                    invalidIds.add(id);
                }
            }
        }
        return invalidIds.stream().mapToLong(Long::longValue).sum();
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        for (String range: input.split(",")) {
            String[] vals = range.split("-");
            long startId = Long.parseLong(vals[0]);
            long endId  = Long.parseLong(vals[1]);
            result1 += sumInvalidIds1(startId, endId);
            result2 += sumInvalidIds2(startId, endId);
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_02::solve, true);
        Helpers.runTask(MAIN, Day_02::solve, false);
    }
}

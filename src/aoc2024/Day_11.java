package aoc2024;

import org.eclipse.collections.api.factory.primitive.LongLongMaps;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import util.Resources;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Day_11 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/11-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/11-main.txt");


    private static final int NUM_BLINKS_1 = 25;
    private static final int NUM_BLINKS_2 = 75;

    private static int stringSize(long x) {
        long p = 10;
        for (int i = 1; i < 19; i++) {
            if (x < p) {
                return i;
            }
            p = 10 * p;
        }
        return 19;
    }

    private static long pow10(int exp) {
        long p = 10;
        for (int i = 1; i < exp; i++) {
            p = 10 * p;
        }
        return p;
    }

    private static long getNumStones(long[] stones, int numBlinks) {
        MutableLongLongMap mapCurr = LongLongMaps.mutable.withInitialCapacity(stones.length);
        for (long stone : stones) {
            mapCurr.addToValue(stone, 1L);
        }
        for (int i = 0; i < numBlinks; i++) {
            // Reserve twice as much size
            MutableLongLongMap mapNew = LongLongMaps.mutable.withInitialCapacity(mapCurr.size() * 2);
            mapCurr.forEachKeyValue((num, occurrences) -> {
                if (num == 0) {
                    mapNew.addToValue(1L, occurrences);
                } else {
                    int len = stringSize(num);
                    if (len % 2 == 0) {
                        long pow10 = pow10(len / 2);
                        long num1 = num / pow10;
                        long num2 = num % pow10;
                        mapNew.addToValue(num1, occurrences);
                        mapNew.addToValue(num2, occurrences);
                    } else {
                        mapNew.addToValue(num * 2024, occurrences);
                    }
                }
            });
            mapCurr = mapNew;
        }
        return mapCurr.sum();
    }

    public static void main(String[] args) throws IOException {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        LongLongMaps.mutable.empty(); // Warmup

        Instant start = Instant.now();

        long[] arr = Arrays.stream(input.trim().split(" ")).mapToLong(Long::parseLong).toArray();

        long result1 = getNumStones(arr, NUM_BLINKS_1);
        long result2 = getNumStones(arr, NUM_BLINKS_2);

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 183620) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 220377651399268L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

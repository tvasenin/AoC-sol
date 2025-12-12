package aoc2024;

import org.eclipse.collections.api.factory.primitive.LongLongMaps;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;

public class Day_11 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/11-test.txt"), 55312, 65601038650482L);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/11-main.txt"), 183620, 220377651399268L);


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

    public static TaskSolution solve(String input) {
        long[] arr = Arrays.stream(input.trim().split(" ")).mapToLong(Long::parseLong).toArray();

        long result1 = getNumStones(arr, NUM_BLINKS_1);
        long result2 = getNumStones(arr, NUM_BLINKS_2);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        LongLongMaps.mutable.empty(); // Warmup
        Helpers.runTask(TEST, Day_11::solve, false);
        Helpers.runTask(MAIN, Day_11::solve, false);
    }
}

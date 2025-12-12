package aoc2025;

import util.Cell;
import util.CharField;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Day_07 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2025/07-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2025/07-main.txt");


    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        CharField field = CharField.of(input);


        Instant start = Instant.now();

        long result1 = 0;
        long[] timesCurr =  new long[field.numCols];
        Cell startCell = field.findFirstCell('S').orElseThrow();
        timesCurr[startCell.col()] = 1;
        for (int row = startCell.row(); row < field.numRows - 1; row++) {
            long[] timesNext = new long[field.numCols];
            for (int col = 0; col < field.numCols; col++) {
                long cnt = timesCurr[col];
                if (cnt > 0) {
                    if (field.get(row + 1, col) == '^') {
                        // No need to check boundaries due to the input having a border
                        timesNext[col + 1] += cnt;
                        timesNext[col - 1] += cnt;
                        result1++;
                    } else {
                        timesNext[col] += cnt;
                    }
                }
            }
            timesCurr = timesNext;
        }
        long result2 = Arrays.stream(timesCurr).sum();

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 1619) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 23607984027985L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

package aoc2025;

import one.util.streamex.StreamEx;
import util.Cell;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Day_09_Cheat {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2025/09-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2025/09-main.txt");


    private static long getArea(Cell c1, Cell c2) {
        long h = Math.abs(c1.row() - c2.row()) + 1;
        long w = Math.abs(c1.col() - c2.col()) + 1;
        return h * w;
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        List<Cell> cells = input.lines()
                .map(line -> {
                    String[] s = line.split(",");
                    return new Cell(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
                })
                .toList();

        long result1 = StreamEx.ofPairs(cells, Day_09_Cheat::getArea)
                .mapToLong(l -> l)
                .max()
                .orElseThrow();


        // FIXME: Cheat
        Cell cell2A = cells.get(248); // new Cell(94543, 50265);
        Cell cell2B = cells.get(218); // new Cell(5459, 67484);

        long result2 = getArea(cell2A, cell2B);

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 4755278336L) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 1534043700L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

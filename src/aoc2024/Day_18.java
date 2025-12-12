package aoc2024;

import org.apache.commons.lang3.StringUtils;
import util.Cell;
import util.Helpers;
import util.Resources;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Day_18 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/18-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/18-main.txt");

    private static final int SIZE = 71;
    @SuppressWarnings("unused")
    private static final int SIZE_TEST = 7;

    private static final int LIMIT_1 = 1024;
    @SuppressWarnings("unused")
    private static final int LIMIT_TEST_1 = 12;


    private static int getCellObj(int[][] field, Cell cell) {
        return field[cell.row()][cell.col()];
    }

    private static boolean setVisitedCell(int[][] visited, Cell cell, int newValue) {
        int oldValue = visited[cell.row()][cell.col()];
        if (newValue < oldValue) {
            visited[cell.row()][cell.col()] = newValue;
            return true;
        }
        return false;
    }

    private static long getMinPathLength(Set<Cell> walls, int size, Cell start, Cell end) {
        Queue<Cell> queue = new LinkedList<>();
        int[][] visited = new int[size][size];
        for (int row = 0; row < size; row++) {
            Arrays.fill(visited[row], Integer.MAX_VALUE);
        }
        setVisitedCell(visited, start, 0);
        queue.add(start);
        while (!queue.isEmpty()) {
            Cell curCell = queue.poll();
            if (curCell == end) {
                continue;
            }
            int newWeight = getCellObj(visited, curCell) + 1;
            Helpers.getValidNeighborsStream(curCell, size, size, true)
                    .filter(tryCell -> !walls.contains(tryCell))
                    .forEachOrdered(nextCell -> {
                        if (setVisitedCell(visited, nextCell, newWeight)) {
                            queue.add(nextCell);
                        }
                    });
        }
        return getCellObj(visited, end);
    }

    private static Cell getFirstCuttingWall(List<Cell> walls, int size, Cell start, Cell end, int startLimit) {
        for (int limit = startLimit; limit < walls.size(); limit++) {
            Set<Cell> walls2 = new HashSet<>(walls.subList(0, limit));
            long minPath = getMinPathLength(walls2, size, start, end);
            if (minPath == Integer.MAX_VALUE) {
                return walls.get(limit - 1);
            }
        }
        throw new RuntimeException("No cutting wall found");
    }

    public static void main(String[] args) throws IOException {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;
        //noinspection ConstantValue
        int size = isTest ? SIZE_TEST : SIZE;
        //noinspection ConstantValue
        int limit = isTest ? LIMIT_TEST_1 : LIMIT_1;

        Instant start = Instant.now();

        List<Cell> walls = input.lines()
                .map(s -> {
                    String[] s0 = s.split(",");
                    return new Cell(Integer.parseInt(s0[1]), Integer.parseInt(s0[0]));
                })
                .toList();

        Cell startCell = new Cell(0, 0);
        Cell endCell = new Cell(size - 1, size - 1);
        Set<Cell> walls1 = new HashSet<>(walls.subList(0, limit));
        long result1 = getMinPathLength(walls1, size, startCell, endCell);

        Cell cuttingCell = getFirstCuttingWall(walls, size, startCell, endCell, limit);

        String result2 = StringUtils.joinWith(",", cuttingCell.col(), cuttingCell.row());

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        if (!isTest) {
            if (result1 != 304) {
                System.out.println("Wrong Result 1");
            }
            if (!result2.equals("50,28")) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

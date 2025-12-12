package aoc2024;

import util.Cell;
import util.Helpers;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day_08 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/08-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/08-main.txt");


    private static List<Cell> getAntiNodesDirectional(Cell cell, int dRow, int dCol, int numRows, int numCols) {
        List<Cell> antiNodes = new ArrayList<>();
        int k = 0;
        do {
            Cell antiNode = new Cell(cell.row() + k * dRow, cell.col() + k * dCol);
            if (!Helpers.isWithinField(antiNode, numRows, numCols)) {
                break;
            }
            antiNodes.add(antiNode);
            k++;
        } while (true);
        return antiNodes;
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        Map<Character, List<Cell>> groups = new HashMap<>();

        String[] lines = input.split("\n");
        int numRows = lines.length;
        int numCols = lines[0].length();
        for (int i = 0; i < numRows; i++) {
            String line = lines[i];
            for (int j = 0; j < numCols; j++) {
                groups.computeIfAbsent(line.charAt(j), k -> new ArrayList<>()).add(new Cell(i, j));
            }
        }
        groups.remove('.');

        Set<Cell> antiNodes1 = new HashSet<>();
        Set<Cell> antiNodes2 = new HashSet<>();

        for (List<Cell> group : groups.values()) {
            for (int i = 0; i < group.size() - 1; i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    Cell c1 = group.get(i);
                    Cell c2 = group.get(j);
                    int dRow = c2.row() - c1.row();
                    int dCol = c2.col() - c1.col();
                    List<Cell> antiRes1 = getAntiNodesDirectional(c1, -dRow, -dCol, numRows, numCols);
                    List<Cell> antiRes2 = getAntiNodesDirectional(c2, +dRow, +dCol, numRows, numCols);
                    antiNodes2.addAll(antiRes1);
                    antiNodes2.addAll(antiRes2);
                    if (antiRes1.size() > 1) antiNodes1.add(antiRes1.get(1));
                    if (antiRes2.size() > 1) antiNodes1.add(antiRes2.get(1));
                }
            }
        }

        long result1 = antiNodes1.size();
        long result2 = antiNodes2.size();

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 295) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 1034) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

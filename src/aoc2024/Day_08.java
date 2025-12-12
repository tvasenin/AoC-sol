package aoc2024;

import util.Cell;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day_08 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/08-test.txt"), 14, 34);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/08-main.txt"), 295, 1034);


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

    public static TaskSolution solve(String input) {
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

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_08::solve, false);
        Helpers.runTask(MAIN, Day_08::solve, false);
    }
}

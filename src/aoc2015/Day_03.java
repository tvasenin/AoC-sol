package aoc2015;

import util.Cell;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.HashSet;
import java.util.Set;

public class Day_03 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2015/03-main.txt"), 2572, 2631);


    private static Cell applyDirection(Cell cell, char direction) {
        return switch (direction) {
            case '^' -> new Cell(cell.row() - 1, cell.col());
            case '<' -> new Cell(cell.row(), cell.col() - 1);
            case 'v' -> new Cell(cell.row() + 1, cell.col());
            case '>' -> new Cell(cell.row(), cell.col() + 1);
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    public static TaskSolution solve(String input) {
        Cell curCell1 = new Cell(0, 0);
        Cell curCell2A = new Cell(0, 0);
        Cell curCell2B = new Cell(0, 0);
        Set<Cell> visitedCells1 = new HashSet<>();
        visitedCells1.add(curCell1);
        Set<Cell> visitedCells2 = new HashSet<>();
        visitedCells2.add(curCell2A);
        for (int i = 0, n = input.length() ; i < n ; i++) {
            char c = input.charAt(i);
            curCell1 = applyDirection(curCell1, c);
            visitedCells1.add(curCell1);
            if (i % 2 == 0) {
                curCell2A = applyDirection(curCell2A, c);
                visitedCells2.add(curCell2A);
            } else {
                curCell2B = applyDirection(curCell2B, c);
                visitedCells2.add(curCell2B);
            }
        }
        long result1 = visitedCells1.size();
        long result2 = visitedCells2.size();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        if (!solve("<>").equals(TaskSolution.of(2, 3))) { throw new AssertionError(); } // Warmup
        Helpers.runTask(MAIN, Day_03::solve);
    }
}

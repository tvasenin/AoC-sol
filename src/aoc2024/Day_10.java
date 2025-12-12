package aoc2024;

import util.Cell;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Day_10 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/10-test.txt"), 36, 81);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/10-main.txt"), 782, 1694);


    public static TaskSolution solve(String input) {
        int[][] field = Helpers.readIntField(input);
        int numRows = field.length;
        int numCols = field[0].length;

        long result1 = 0, result2 = 0;

        int[][] weights = new int[numRows][numCols];
        Set<Cell> oldCells = new HashSet<>();

        for (int startRow = 0; startRow < numRows; startRow++) {
            for (int startCol = 0; startCol < numCols; startCol++) {
                int height = field[startRow][startCol];
                if (height == 0) {
                    // No need to clear old weights
                    weights[startRow][startCol] = 1;

                    oldCells.clear();
                    oldCells.add(new Cell(startRow, startCol));

                    for (int h = 1; h <= 9; h++) {
                        int finalH = h;
                        Set<Cell> newCells = oldCells.stream()
                                .flatMap(cell -> Helpers.getValidNeighborsStream(cell, numRows, numCols, true))
                                .filter(cell -> field[cell.row()][cell.col()] == finalH)
                                .collect(Collectors.toSet());
                        for (Cell cell : newCells) {
                            int newWeight = Helpers.getValidNeighborsStream(cell, numRows, numCols, true)
                                    .filter(oldCells::contains)
                                    .mapToInt(oldCell -> weights[oldCell.row()][oldCell.col()])
                                    .sum();
                            weights[cell.row()][cell.col()] = newWeight;
                        }
                        oldCells = newCells;
                    }

                    int score = oldCells.size();
                    int rating = oldCells.stream().mapToInt(cell -> weights[cell.row()][cell.col()]).sum();

                    result1 += score;
                    result2 += rating;
                }
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_10::solve, false);
        Helpers.runTask(MAIN, Day_10::solve, false);
    }
}

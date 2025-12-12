package aoc2025;

import util.Cell;
import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Iterator;
import java.util.List;

public class Day_04 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/04-test.txt"), 13, 43);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/04-main.txt"), 1351, 8345);


    private static int numNeighbors(CharField field, int row, int col) {
        return field.countNeighbors(row, col, '@', false);
    }

    private static long getNumRolls1(CharField field) {
        long result = 0;
        for (int row = 0; row < field.numRows; row++) {
            for (int col = 0; col < field.numCols; col++) {
                if (field.field[row][col] == '.') {
                    continue;
                }
                if (numNeighbors(field, row, col) < 4) {
                    result++;
                }
            }
        }
        return result;
    }

    private static int[][] getNeighborCountIntField(CharField field) {
        int[][] result = new int[field.numRows][field.numCols];
        for (int row = 0; row < field.numRows; row++) {
            for (int col = 0; col < field.numCols; col++) {
                result[row][col] = numNeighbors(field, row, col);
            }
        }
        return result;
    }

    private static long getNumRolls2(CharField field) {
        int[][] refcounts = getNeighborCountIntField(field);
        List<Cell> items = field.findAllCells('@');
        long result = 0;
        boolean removed;
        do {
            removed = false;
            Iterator<Cell> it = items.iterator();
            while (it.hasNext()) {
                Cell cell = it.next();
                if (refcounts[cell.row()][cell.col()] < 4) {
                    // We can remove item immediately, no need to wait for the end of the pass
                    removed = true;
                    result++;
                    field
                            .getNeighborsStream(cell, false)
                            .forEach(neighbor -> refcounts[neighbor.row()][neighbor.col()]--);
                    it.remove();
                }
            }
        } while (removed);
        return result;
    }

    public static TaskSolution solve(String input) {
        CharField field = CharField.of(input);

        long result1 = getNumRolls1(field);
        long result2 = getNumRolls2(field);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_04::solve, true);
        Helpers.runTask(MAIN, Day_04::solve, false);
    }
}

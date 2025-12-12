package aoc2017;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_03 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2017/03-main.txt"), 419, 295229);


    private static int getNumSteps(int n) {
        if (n == 1) return 0;
        int idx = n - 1;
        int r = (((int) Math.floor(Math.sqrt(idx)) + 1) / 2);
        int firstOuter = (((r - 1) * 2 + 1)  * ((r - 1) * 2 + 1));
        int quarter = r * 2;
        int arc = (idx - firstOuter) % quarter;
        int numStepsArc = Math.abs(arc - (r - 1));
        return r + numStepsArc;
    }

    private static int getIdx(int dim, int row, int col) {
        return row * dim + col;
    }

    private static int getSumNeighbors(int[] grid, int stride, int row, int col) {
        int sum = 0;
        for (int r = row - 1; r < row + 2 ; r++) {
            for (int c = col - 1; c < col + 2 ; c++) {
                sum += grid[getIdx(stride, r, c)];
            }
        }
        sum -= grid[getIdx(stride, row, col)];
        return sum;
    }

    private static int updateWithNeighborsSum(int[] grid, int dim, int row, int col) {
        int sum = getSumNeighbors(grid, dim, row, col);
        grid[getIdx(dim, row, col)] = sum;
        return sum;
    }

    private static int getFirstLargerCellValue(int input) {
        // Crude estimation for array dimension upper bound
        // Add +2 extra cells for neighbors
        int dim = (int)Math.floor(Math.sqrt(input - 1)) + 1 + 2;
        int[] grid = new int[dim*dim];
        int r = dim / 2;
        int c = dim / 2;
        int quarter = 0;
        grid[getIdx(dim, r, c)] = 1;
        do {
            for (int i = 0; i < quarter; i++) {
                r--;
                int cellValue = updateWithNeighborsSum(grid, dim, r, c);
                if (cellValue > input) {
                    return cellValue;
                }
            }
            for (int i = 0; i < quarter; i++) {
                c--;
                int cellValue = updateWithNeighborsSum(grid, dim, r, c);
                if (cellValue > input) {
                    return cellValue;
                }
            }
            for (int i = 0; i < quarter; i++) {
                r++;
                int cellValue = updateWithNeighborsSum(grid, dim, r, c);
                if (cellValue > input) {
                    return cellValue;
                }
            }
            for (int i = 0; i < quarter; i++) {
                c++;
                int cellValue = updateWithNeighborsSum(grid, dim, r, c);
                if (cellValue > input) {
                    return cellValue;
                }
            }
            c++;
            r++;
            quarter += 2;
        } while (true);
    }

    public static TaskSolution solve(String input) {
        int cellId = Integer.parseUnsignedInt(input);
        long result1 = getNumSteps(cellId);
        long result2 = getFirstLargerCellValue(cellId);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(MAIN, Day_03::solve, false);
    }
}

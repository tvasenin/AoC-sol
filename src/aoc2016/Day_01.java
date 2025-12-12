package aoc2016;

import util.Cell;
import util.Direction;
import util.GridWalker;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class Day_01 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2016/01-main.txt"), 253, 126);


    public static TaskSolution solve(String input) {
        GridWalker walker = new GridWalker(0, 0, Direction.U);

        Set<Cell> visitedCells = new HashSet<>();
        visitedCells.add(walker.getCell());

        AtomicReference<Cell> firstAlreadyVisitedCell = new AtomicReference<>();

        Arrays.stream(input.split(", ")).forEachOrdered(cmd -> {
            char dirCode = cmd.charAt(0);
            switch (dirCode) {
                case 'L' -> walker.rotateCCW();
                case 'R' -> walker.rotateCW();
                default -> throw new IllegalStateException("Unexpected value: " + dirCode);
            }
            int numSteps = Integer.parseInt(cmd.substring(1));
            for (int i = 0; i < numSteps; i++) {
                walker.moveForward(1);
                Cell newCell = walker.getCell();
                boolean isNew = visitedCells.add(newCell);
                if (firstAlreadyVisitedCell.get() == null && !isNew) {
                    firstAlreadyVisitedCell.set(newCell);
                }
            }
        });

        Cell finalCell = walker.getCell();

        long result1 = Math.abs(finalCell.row()) + Math.abs(finalCell.col());
        long result2 = Math.abs(firstAlreadyVisitedCell.get().row()) + Math.abs(firstAlreadyVisitedCell.get().col());

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(MAIN, Day_01::solve, true);
    }
}

package aoc2024;

import util.CharFieldLinear;
import util.Direction;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Day_16 {

    private static final TaskData TEST_1 = new TaskData(Resources.getResourceAsString("aoc2024/16-test-1.txt"), 7036, 45);
    private static final TaskData TEST_2 = new TaskData(Resources.getResourceAsString("aoc2024/16-test-2.txt"), 11048, 64);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/16-main.txt"), 108504, 538);


    private record WeightedMove(int cell, Direction dir, long weight) { }

    private static boolean setVisitedMove(long[][] visited, int cell, Direction dir, long newValue) {
        long oldValue = visited[cell][dir.ordinal()];
        if (newValue <= oldValue) {
            visited[cell][dir.ordinal()] = newValue;
            return true;
        }
        return false;
    }

    private static void setVisitedMove(long[] weights, Direction dir, long newValue) {
        long oldValue = weights[dir.ordinal()];
        if (newValue <= oldValue) {
            weights[dir.ordinal()] = newValue;
        }
    }

    private static long findAllWinningCells(CharFieldLinear field, int startCell, int endCell, Set<Integer> winningCells) {
        long[][] weights = getWeights(field, startCell, endCell);
        //noinspection UnnecessaryLocalVariable
        long bestWeight = collectWinningCells(field, weights, startCell, endCell, winningCells);
        return bestWeight;
    }

    private static long[][] getWeights(CharFieldLinear field, int startCell, int endCell) {
        Queue<WeightedMove> queue = new ArrayDeque<>();
        long[][] weights = new long[field.field.length][4];
        for (long[] longs : weights) {
            Arrays.fill(longs, Integer.MAX_VALUE);
        }
        WeightedMove startMove = new WeightedMove(startCell, Direction.R, 0);
        queue.add(startMove);
        while (!queue.isEmpty()) {
            WeightedMove curMove = queue.poll();
            int curCell = curMove.cell;
            long curWeight = curMove.weight;
            Direction curDir = curMove.dir;

            // Update turns
            long[] curWeights = weights[curCell];
            for (Direction newDir : Direction.values()) {
                int numTurns = getNumTurns(curDir, newDir);
                long newWeightAfterTurn = curWeight + (1000L * numTurns);
                setVisitedMove(curWeights, newDir, newWeightAfterTurn);
                // No boundary check because the border in the input is solid
                int nextCell = field.getNextCellIdxNoCheck(curCell, newDir);
                if (field.get(nextCell) != '#') {
                    long newWeightAfterMove = newWeightAfterTurn + 1;
                    if (setVisitedMove(weights, nextCell, newDir, newWeightAfterMove)) {
                        if (nextCell != endCell) {
                            queue.add(new WeightedMove(nextCell, newDir, newWeightAfterMove));
                        }
                    }
                }
            }
        }
        return weights;
    }

    private static int getNumTurns(Direction dirA, Direction dirB) {
        if (dirA == dirB) {
            return 0;
        }
        if ((dirA.ordinal() + dirB.ordinal()) % 2 == 0) {
            return 2;
        }
        return 1;
    }

    private static long collectWinningCells(CharFieldLinear field, long[][] weights, int startCell, int endCell,
                                            Set<Integer> winningCells) {
        // Collect Winning Cells -- walk backwards from exit to start
        Queue<WeightedMove> queueBack = new ArrayDeque<>();

        long[] endDirWeights = weights[endCell];
        long bestWeight = Arrays.stream(endDirWeights).min().orElseThrow();
        for (Direction endDir : Direction.values()) {
            if (endDirWeights[endDir.ordinal()] == bestWeight) {
                queueBack.add(new WeightedMove(endCell, endDir, bestWeight));
            }
        }

        while (!queueBack.isEmpty()) {
            WeightedMove curMove = queueBack.poll();
            int curCell = curMove.cell;
            winningCells.add(curCell);
            if (curCell == startCell) {
                continue;
            }

            long curWeight = curMove.weight();
            Direction curDir = curMove.dir;

            for (Direction newDirFrom : Direction.values()) {
                // No boundary check because the border in the input is solid
                int newCell = field.getNextCellIdxNoCheck(curCell, newDirFrom.reverse());
                long newWeight = weights[newCell][newDirFrom.ordinal()];
                int numTurns = getNumTurns(newDirFrom, curDir);
                long expectedWeight = curWeight - 1 - 1000L * numTurns;
                if (newWeight == expectedWeight) {
                    queueBack.add(new WeightedMove(newCell, newDirFrom, newWeight));
                }
            }
        }
        return bestWeight;
    }

    public static TaskSolution solve(String input) {
        CharFieldLinear field = CharFieldLinear.of(input);

        int startCell = field.findFirstCellIdx('S');
        int endCell = field.findFirstCellIdx('E');

        if (startCell == -1 || endCell == -1) {
            throw new IllegalArgumentException("Invalid input");
        }

        Set<Integer> winningCells = new HashSet<>();

        long result1 = findAllWinningCells(field, startCell, endCell, winningCells);
        long result2 = winningCells.size();

        // use AtomicBoolean to evade IDE ConstantValue warnings
        AtomicBoolean debug = new AtomicBoolean(false);
        if (debug.get()) {
            winningCells.forEach(winningCell -> field.set(winningCell, 'O'));
            Helpers.printCharField(field);
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST_1, Day_16::solve, true);
        Helpers.runTask(TEST_2, Day_16::solve, true);
        Helpers.runTask(MAIN, Day_16::solve, false);
    }
}

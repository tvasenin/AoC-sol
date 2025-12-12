package aoc2023;

import util.Cell;
import util.Direction;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Day_17 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/17-test.txt"), 102, 94);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/17-main.txt"), 936, 1157);


    record Position(int row, int col, Direction dir, int repeat) { }

    record State(Position position, int weight) { }

    private static final Comparator<State> STATE_WEIGHT_COMPARATOR = Comparator.comparingInt(State::weight);

    static Position getNewPosition(Position oldPos, Direction newDir, int numRows, int numCols,
                                   int minBeforeTurn, int maxWithoutTurn) {
        boolean turnHappened = oldPos.dir != newDir;
        final int newRepeat;
        if (turnHappened) {
            newRepeat = 0;
            if (oldPos.repeat + 1 < minBeforeTurn) {
                return null;
            }
        } else {
            newRepeat = oldPos.repeat + 1;
            if (newRepeat > maxWithoutTurn - 1) {
                return null;
            }
        }
        int row = oldPos.row;
        int col = oldPos.col;
        switch (newDir) {
            case U -> row--;
            case D -> row++;
            case L -> col--;
            case R -> col++;
        }

        if (!(row > -1 && row < numRows)) {
            return null;
        }
        if (!(col > -1 && col < numCols)) {
            return null;
        }
        return new Position(row, col, newDir, newRepeat);
    }

    static List<Position> getPossibleChoices(Position pos, int numRows, int numCols,
                                             int minBeforeTurn, int maxWithoutTurn) {
        List<Position> choices = new ArrayList<>(3);
        Direction dirF = pos.dir;
        Position newPosF = getNewPosition(pos, dirF, numRows, numCols, minBeforeTurn, maxWithoutTurn);
        if (newPosF != null) {
            choices.add(newPosF);
        }
        Direction dirL = dirF.rotateCCW();
        Position newPosL = getNewPosition(pos, dirL, numRows, numCols, minBeforeTurn, maxWithoutTurn);
        if (newPosL != null) {
            choices.add(newPosL);
        }
        Direction dirR = dirF.rotateCW();
        Position newPosR = getNewPosition(pos, dirR, numRows, numCols, minBeforeTurn, maxWithoutTurn);
        if (newPosR != null) {
            choices.add(newPosR);
        }
        return choices;
    }

    static int getMinWeight(int[][] field, Cell start, Cell end, int minBeforeTurn, int maxWithoutTurn) {
        int numRows = field.length;
        int numCols = field[0].length;
        int numDirs = Direction.values().length;
        int[][][][] bestWeights = new int[numRows][numCols][numDirs][maxWithoutTurn];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                for (int k = 0; k < numDirs; k++) {
                    Arrays.fill(bestWeights[i][j][k], Integer.MAX_VALUE);
                }
            }
        }
        int minWeight = Integer.MAX_VALUE;
        Queue<State> queue = new PriorityQueue<>(STATE_WEIGHT_COMPARATOR);
        for (Direction dir : Direction.values()) {
            queue.add(new State(new Position(start.row(), start.col(), dir, 0), 0));
        }
        while (!queue.isEmpty()) {
            State state = queue.remove();
            Position curPos = state.position;
            int curWeight = state.weight;
            List<Position> choices = getPossibleChoices(curPos, numRows, numCols, minBeforeTurn, maxWithoutTurn);
            for (Position newPos : choices) {
                int newWeight = curWeight + field[newPos.row][newPos.col];
                int bestWeight = bestWeights[newPos.row][newPos.col][newPos.dir.ordinal()][newPos.repeat];
                if (newWeight < bestWeight) {
                    bestWeights[newPos.row][newPos.col][newPos.dir.ordinal()][newPos.repeat] = newWeight;
                    if (newPos.row == end.row() && newPos.col == end.col()) {
                        minWeight = Math.min(minWeight, newWeight);
                    } else {
                        queue.add(new State(newPos, newWeight));
                    }
                }
            }
        }
        return minWeight;
    }

    public static TaskSolution solve(String input) {
        int[][] field = Helpers.readIntField(input);
        int numRows = field.length;
        int numCols = field[0].length;

        Cell startCell = new Cell(0, 0);
        Cell endCell = new Cell(numRows - 1, numCols - 1);

        long result1 = getMinWeight(field, startCell, endCell, 0, 3);
        long result2 = getMinWeight(field, startCell, endCell, 4, 10);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_17::solve, false);
        Helpers.runTask(MAIN, Day_17::solve, false);
    }
}

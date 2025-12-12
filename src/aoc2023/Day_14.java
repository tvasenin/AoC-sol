package aoc2023;

import util.CharField;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day_14 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/14-test.txt"), 136, 64);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/14-main.txt"), 113078, 94255);


    private static void tiltNorth(CharField field) {
        char[][] innerField = field.field;
        for (int i = 0; i < field.numRows; i++) {
            for (int j = 0; j < field.numCols; j++) {
                char c = innerField[i][j];
                if (c == 'O') {
                    int k = i;
                    while (k > 0 && innerField[k-1][j] == '.') {
                        k--;
                    }
                    if (k != i) {
                        innerField[k][j] = 'O';
                        innerField[i][j] = '.';
                    }
                }
            }
        }
    }

    private static void tiltSouth(CharField field) {
        char[][] innerField = field.field;
        for (int i = field.numRows - 2; i > -1; i--) {
            for (int j = 0; j < field.numCols; j++) {
                char c = innerField[i][j];
                if (c == 'O') {
                    int k = i;
                    while (k < field.numRows - 1 && innerField[k+1][j] == '.') {
                        k++;
                    }
                    if (k != i) {
                        innerField[k][j] = 'O';
                        innerField[i][j] = '.';
                    }
                }
            }
        }
    }


    private static void tiltWest(CharField field) {
        char[][] innerField = field.field;
        for (int i = 0; i < field.numRows; i++) {
            for (int j = 0; j < field.numCols; j++) {
                char c = innerField[i][j];
                if (c == 'O') {
                    int k = j;
                    while (k > 0 && innerField[i][k-1] == '.') {
                        k--;
                    }
                    if (k != j) {
                        innerField[i][k] = 'O';
                        innerField[i][j] = '.';
                    }
                }
            }
        }
    }

    private static void tiltEast(CharField field) {
        char[][] innerField = field.field;
        for (int i = 0; i < field.numRows; i++) {
            for (int j = field.numCols - 2; j > -1; j--) {
                char c = innerField[i][j];
                if (c == 'O') {
                    int k = j;
                    while (k < field.numCols - 1 && innerField[i][k+1] == '.') {
                        k++;
                    }
                    if (k != j) {
                        innerField[i][k] = 'O';
                        innerField[i][j] = '.';
                    }
                }
            }
        }
    }

    private static long getWeights(CharField field) {
        char[][] innerField = field.field;
        long weight = 0;
        for (int i = 0; i < field.numRows; i++) {
            for (int j = 0; j < field.numCols; j++) {
                if (innerField[i][j] == 'O') {
                    weight += field.numRows - i;
                }
            }
        }
        return weight;
    }

    private static IntBuffer getKey(CharField field) {
        char[][] innerField = field.field;
        List<Integer> pos = new ArrayList<>();
        for (int i = 0; i < field.numRows; i++) {
            for (int j = 0; j < field.numCols; j++) {
                if (innerField[i][j] == 'O') {
                    pos.add(i * field.numRows + j);
                }
            }
        }
        return IntBuffer.wrap(pos.stream().mapToInt(i -> i).toArray());
    }

    public static TaskSolution solve(String input) {
        CharField field = CharField.of(input);

        CharField tilted = new CharField(field);

        tiltNorth(tilted);
        long result1 = getWeights(tilted);

        Set<IntBuffer> cache = new HashSet<>();

        int numCycles = 1_000_000_000;

        boolean cycleStartFound = false;

        for (int i = 0; i < numCycles; i++) {
            if (i % 100_000 == 0) {
                System.out.println("Step " + i);
            }
            tiltNorth(field);
            tiltWest(field);
            tiltSouth(field);
            tiltEast(field);

            if (!cache.add(getKey(field))) {
                if (!cycleStartFound) {
                    cycleStartFound = true;
                } else {
                    int cycleLength = cache.size();
                    int cyclesLeft = numCycles - i;
                    int cyclesToSkip = cyclesLeft / cycleLength;
                    i = i + cycleLength * cyclesToSkip;
                }
                cache.clear();
                cache.add(getKey(field));
            }
        }

        long result2 = getWeights(field);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_14::solve, true);
        Helpers.runTask(MAIN, Day_14::solve, false);
    }
}

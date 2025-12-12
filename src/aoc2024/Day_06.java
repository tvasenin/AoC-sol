package aoc2024;

import util.CharFieldLinear;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.List;

public class Day_06 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/06-test.txt"), 41, 6);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/06-main.txt"), 5531, 2165);


    private static boolean simulateSteps(CharFieldLinear field, int curCell) {
        char[] innerField = field.field;
        int curDirOrdinal = 0;
        do {
            char oldState = innerField[curCell];
            char newState = (char) (oldState | 1 << curDirOrdinal);
            if (newState == oldState) {
                // Loop detected
                return true;
            }
            innerField[curCell] = newState;

            // Optimization: Update state only after trying to rotate in all directions
            for (int turn = 0; turn < 4; turn++) {
                int tryCell = field.getNextCellIdxNoCheck(curCell, curDirOrdinal);

                char tryState = innerField[tryCell];

                if (tryState == 'X') {
                    // Went out of bounds
                    return false;
                }

                if (tryState != '#') {
                    // Can move there
                    curCell = tryCell;
                    break;
                }

                // Rotate CW
                curDirOrdinal = (curDirOrdinal + 3) % 4;
            }
        } while (true);
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;

        // Pad to simplify detect out-of-bounds detection
        CharFieldLinear refField = CharFieldLinear.of(input, 1, 'X');
        int startCell = refField.findFirstCellIdx('^');
        if (startCell == -1) {
            throw new IllegalArgumentException("Invalid input");
        }

        // Use field to store moves
        refField.replaceAllExcept((char) 0, '#', 'X'); // Clear field
        CharFieldLinear field = new CharFieldLinear(refField);

        simulateSteps(field, startCell);
        List<Integer> wallCandidates = new ArrayList<>();
        for (int cell = 0; cell < field.field.length; cell++) {
            char f = field.get(cell);
            if (f != '#' && f != 'X' && f != (char) 0) {
                result1++;
                if (cell != startCell) {
                    wallCandidates.add(cell);
                }
            }
        }

        for (int wall : wallCandidates) {
            field.copyFrom(refField);
            field.set(wall, '#');
            boolean isLoop = simulateSteps(field, startCell);
            if (isLoop) {
                result2++;
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_06::solve, true);
        Helpers.runTask(MAIN, Day_06::solve);
    }
}

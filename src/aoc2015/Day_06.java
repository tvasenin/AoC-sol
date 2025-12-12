package aoc2015;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.BitSet;

public class Day_06 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2015/06-main.txt"), 377891, 14110788);


    private enum Action {
        TURN_ON,
        TURN_OFF,
        TOGGLE
    }

    private static final int DIM = 1000;

    public static TaskSolution solve(String input) {
        BitSet grid1 = new BitSet(DIM * DIM);
        int[] grid2 = new int[DIM * DIM];
        for (String line : input.split("\n")) {
            String[] s0 = line.split(" ");
            String[] sFrom = s0[s0.length - 3].split(",");
            String[] sTo = s0[s0.length - 1].split(",");
            int sFromCol = Integer.parseInt(sFrom[0]);
            int sFromRow = Integer.parseInt(sFrom[1]);
            int sToCol = Integer.parseInt(sTo[0]) + 1;
            int sToRow = Integer.parseInt(sTo[1]) + 1;
            Action action = switch (s0[0]) {
                case "toggle" -> {
                    if (s0.length != 4) {
                        throw new IllegalStateException("Unexpected line: " + line);
                    }
                    yield Action.TOGGLE;
                }
                case "turn" -> switch (s0[1]) {
                    case "on" -> Action.TURN_ON;
                    case "off" -> Action.TURN_OFF;
                    default -> throw new IllegalStateException("Unexpected line: " + line);
                };
                default -> throw new IllegalStateException("Unexpected line: " + line);
            };
            // Apply action
            for (int r = sFromRow; r < sToRow; r++) {
                int idxFrom = r * DIM + sFromCol;
                int idxTo = r * DIM + sToCol;
                switch (action) {
                    case TURN_ON -> {
                        grid1.set(idxFrom, idxTo);
                        for (int idx = idxFrom; idx < idxTo; idx++) {
                            grid2[idx] += 1;
                        }
                    }
                    case TURN_OFF -> {
                        grid1.clear(idxFrom, idxTo);
                        for (int idx = idxFrom; idx < idxTo; idx++) {
                            grid2[idx] = Math.max(grid2[idx] - 1 , 0);
                        }
                    }
                    case TOGGLE -> {
                        grid1.flip(idxFrom, idxTo);
                        for (int idx = idxFrom; idx < idxTo; idx++) {
                            grid2[idx] += 2;
                        }
                    }
                }
            }
        }
        int result1 = grid1.cardinality();
        int result2 = Arrays.stream(grid2).sum();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(MAIN, Day_06::solve, false);
    }
}

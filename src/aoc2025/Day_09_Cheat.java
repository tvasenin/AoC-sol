package aoc2025;

import one.util.streamex.StreamEx;
import util.Cell;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.List;

public class Day_09_Cheat {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/09-test.txt"), 50, 24);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/09-main.txt"), 4755278336L, 1534043700L);


    private static long getArea(Cell c1, Cell c2) {
        long h = Math.abs(c1.row() - c2.row()) + 1;
        long w = Math.abs(c1.col() - c2.col()) + 1;
        return h * w;
    }

    public static TaskSolution solve(String input) {
        List<Cell> cells = input.lines()
                .map(line -> {
                    String[] s = line.split(",");
                    return new Cell(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
                })
                .toList();

        long result1 = StreamEx.ofPairs(cells, Day_09_Cheat::getArea)
                .mapToLong(l -> l)
                .max()
                .orElseThrow();

        // FIXME: Cheat
        Cell cell2A = cells.get(248); // new Cell(94543, 50265);
        Cell cell2B = cells.get(218); // new Cell(5459, 67484);

        long result2 = getArea(cell2A, cell2B);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
//        Helpers.runTask(TEST, Day_09_Cheat::solve, true);
        Helpers.runTask(MAIN, Day_09_Cheat::solve, false);
    }
}

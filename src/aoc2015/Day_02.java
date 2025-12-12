package aoc2015;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_02 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2015/02-main.txt"), 1598415, 3812909);


    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            String[] dimsString = line.split("x");
            long l = Long.parseLong(dimsString[0]);
            long w = Long.parseLong(dimsString[1]);
            long h = Long.parseLong(dimsString[2]);
            long sideA = l * w;
            long sideB = w * h;
            long sideC = h * l;
            result1 += (sideA * 2) + (sideB * 2) + (sideC * 2) + Math.min(Math.min(sideA, sideB), sideC);
            long perimeterA = (l + w) * 2;
            long perimeterB = (w + h) * 2;
            long perimeterC = (h + l) * 2;
            long lenBow = l * w * h;
            result2 += Math.min(Math.min(perimeterA, perimeterB), perimeterC) + lenBow;
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(MAIN, Day_02::solve, true);
    }
}

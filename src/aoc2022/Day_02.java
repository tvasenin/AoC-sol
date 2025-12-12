package aoc2022;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_02 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2022/02-test.txt"), 15, 12);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2022/02-main.txt"), 14375, 10274);


    @SuppressWarnings("PointlessArithmeticExpression")
    private static int getScore1(String line) {
        // A, X - Rock
        // B, Y - Paper
        // C, Z - Scissors
        return switch (line) {
            case "A X" -> 1 + 3;
            case "A Y" -> 2 + 6;
            case "A Z" -> 3 + 0;
            case "B X" -> 1 + 0;
            case "B Y" -> 2 + 3;
            case "B Z" -> 3 + 6;
            case "C X" -> 1 + 6;
            case "C Y" -> 2 + 0;
            case "C Z" -> 3 + 3;
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private static int getScore2(String line) {
        // A - Rock
        // B - Paper
        // C - Scissors
        // X - Lose
        // Y - Draw
        // Z - Win
        return switch (line) {
            case "A X" -> 3 + 0;
            case "A Y" -> 1 + 3;
            case "A Z" -> 2 + 6;
            case "B X" -> 1 + 0;
            case "B Y" -> 2 + 3;
            case "B Z" -> 3 + 6;
            case "C X" -> 2 + 0;
            case "C Y" -> 3 + 3;
            case "C Z" -> 1 + 6;
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            result1 += getScore1(line);
            result2 += getScore2(line);
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_02::solve, true);
        Helpers.runTask(MAIN, Day_02::solve, true);
    }
}

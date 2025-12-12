package aoc2023;

import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Day_02 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/02-test.txt"), 8, 2286);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/02-main.txt"), 2563, 70768);


    private static final Take TOTAL = new Take(12, 13, 14);

    private record Take(int R, int G, int B) {}

    private static Take parseTake(String takeStr) {
        Iterator<String> it = Arrays.stream(StringUtils.split(takeStr, " ,")).iterator();
        int r = 0, g = 0, b = 0;
        while (it.hasNext()) {
            int num = Integer.parseInt(it.next());
            String col = it.next();
            switch (col) {
                case "red":   r += num; break;
                case "green": g += num; break;
                case "blue":  b += num; break;
            }
        }
        return new Take(r, g, b);
    }

    private static List<Take> parseTakes(String takesStr) {
        String[] takeStrings = StringUtils.split(takesStr, ";");
        return Arrays.stream(takeStrings).map(Day_02::parseTake).toList();
    }

    private static Take getMinUnion(List<Take> takes) {
        int minR = 0, minG = 0, minB = 0;
        for (Take take : takes) {
            minR = Math.max(take.R(), minR);
            minG = Math.max(take.G(), minG);
            minB = Math.max(take.B(), minB);
        }
        return new Take(minR, minG, minB);
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            String[] s1 = line.split(": ");
            int id = Integer.parseInt(s1[0].split(" ")[1]);
            List<Take> takes = parseTakes(s1[1]);
            Take minUnion = getMinUnion(takes);
            if (minUnion.R <= TOTAL.R && minUnion.G <= TOTAL.G && minUnion.B <= TOTAL.B) {
                result1 += id;
            }
            result2 += (long) minUnion.R * minUnion.G * minUnion.B;
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_02::solve, false);
        Helpers.runTask(MAIN, Day_02::solve, false);
    }
}

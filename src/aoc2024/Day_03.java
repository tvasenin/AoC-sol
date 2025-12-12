package aoc2024;

import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day_03 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/03-test.txt"), 161, 48);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/03-main.txt"), 169021493, 111762583);


    private static final Pattern PATTERN = Pattern.compile(StringUtils.joinWith("|", "do\\(\\)", "don't\\(\\)", "mul\\(\\d+,\\d+\\)"));

    private static long getMulResult(String s) {
        String[] s0 = StringUtils.substringBetween(s, "(", ")").split(",");
        long a = Long.parseLong(s0[0]);
        long b = Long.parseLong(s0[1]);
        return a * b;
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;

        List<String> tokens = PATTERN.matcher(input).results().map(MatchResult::group).toList();
        boolean dont = false;
        for (String token : tokens) {
            if (token.equals("don't()")) {
                dont = true;
            } else if (token.equals("do()")) {
                dont = false;
            } else {
                long mul = getMulResult(token);
                result1 += mul;
                if (!dont) {
                    result2 += mul;
                }
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_03::solve, true);
        Helpers.runTask(MAIN, Day_03::solve, true);
    }
}

package aoc2023;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.List;

public class Day_04 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/04-test.txt"), 13, 30);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/04-main.txt"), 20407, 23806951);


    public static TaskSolution solve(String input) {
        List<String> lines = input.lines().toList();

        long result1 = 0, result2 = 0;

        int[] numCards = new int[lines.size()];
        Arrays.fill(numCards, 1);
        for (int i = 0; i < lines.size(); i++) {
            result2 += numCards[i];
            String line = lines.get(i);
            String[] sections = StringUtils.split(line, ":|");

            int[] winningNumbers = Helpers.parseIntArray(sections[1].trim(), "\\s+");
            long numMatches = Arrays.stream(sections[2].trim().split("\\s+"))
                    .mapToInt(Integer::parseInt)
                    .filter(num -> ArrayUtils.contains(winningNumbers, num))
                    .count();
            if (numMatches > 0) {
                result1 += 1L << (numMatches - 1);
                int multiplier = numCards[i];
                for (var j = i + 1; j < i + 1 + numMatches; j++) {
                    numCards[j] += multiplier;
                }
            }
        }
        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_04::solve, true);
        Helpers.runTask(MAIN, Day_04::solve, false);
    }
}

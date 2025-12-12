package aoc2015;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_05 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2015/05-main.txt"), 238, 69);


    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        for (String line : input.split("\n")) {
            int vowelsCnt = 0;
            boolean hasDouble = false;
            boolean hasBadPair = false;
            boolean hasRepeatAround = false;
            boolean hasRepeatingPair = false;
            for (int i = 0, n = line.length(); i < n; i++) {
                char c = line.charAt(i);
                if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                    vowelsCnt++;
                }
                if (i > 0) {
                    char prevChar = line.charAt(i - 1);
                    if (prevChar == c) {
                        hasDouble = true;
                    }
                    // ab, cd, pq, xy
                    boolean badPair1 = prevChar == 'a' && c == 'b';
                    boolean badPair2 = prevChar == 'c' && c == 'd';
                    boolean badPair3 = prevChar == 'p' && c == 'q';
                    boolean badPair4 = prevChar == 'x' && c == 'y';
                    if (badPair1 || badPair2 || badPair3 || badPair4) {
                        hasBadPair = true;
                    }
                    if (i >= 3) {
                        for (int j = 1; j <= i - 2; j++) {
                            char matchChar = line.charAt(j);
                            char prevMatchChar = line.charAt(j - 1);
                            if (c == matchChar && prevChar == prevMatchChar) {
                                hasRepeatingPair = true;
                                break;
                            }
                        }
                    }
                    if (i < n - 1) {
                        char nextChar = line.charAt(i + 1);
                        if (prevChar == nextChar) {
                            hasRepeatAround = true;
                        }
                    }
                }
            }
            boolean isNice1 = vowelsCnt >= 3 && hasDouble && !hasBadPair;
            if (isNice1) {
                result1++;
            }
            boolean isNice2 = hasRepeatingPair && hasRepeatAround;
            if (isNice2) {
                result2++;
            }
        }
        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        if (!solve("aaaa").equals(TaskSolution.of(1, 1))) { throw new AssertionError(); } // Warmup
        Helpers.runTask(MAIN, Day_05::solve);
    }
}

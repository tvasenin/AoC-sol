package aoc2023;

import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

public class Day_06 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/06-test.txt"), 288, 71503);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/06-main.txt"), 2269432, 35865985);


    private static long getNumWins(long t, long d) {
        // n * (t - n) >= d
        // n^2 - t*n + d <= 0
        double sqrtD = Math.sqrt(t * t - 4 * d);
        long nMin = (long) Math.ceil((t - sqrtD) * 0.5);
        long nMax = (long) Math.floor((t + sqrtD) * 0.5);

        nMin = Math.max(nMin, 1);
        nMax = Math.min(nMax, t - 1);

        // Exclude exact record repeating
        if (nMin * (t - nMin) == d) {
            nMin++;
        }
        if (nMax * (t - nMax) == d) {
            nMax--;
        }

        return nMax - nMin + 1;
    }

    public static TaskSolution solve(String input) {
        String[] s0 = input.lines().map(s -> StringUtils.substringAfter(s, ":").trim()).toArray(String[]::new);
        int[] times = Helpers.parseIntArray(s0[0], "\\s+");
        int[] distances = Helpers.parseIntArray(s0[1], "\\s+");

        long result1 = 1;
        for (int i = 0; i < times.length; i++) {
            result1 *= getNumWins(times[i], distances[i]);
        }

        long[] nums2 = input.lines()
                .map(s -> StringUtils.substringAfter(s, ":").replace(" ", ""))
                .mapToLong(Long::parseUnsignedLong)
                .toArray();

        long result2 = getNumWins(nums2[0], nums2[1]);

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_06::solve, true);
        Helpers.runTask(MAIN, Day_06::solve, true);
    }
}

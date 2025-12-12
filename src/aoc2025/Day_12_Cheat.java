package aoc2025;

import org.apache.commons.lang3.StringUtils;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Day_12_Cheat {

    private static final String INPUT = Resources.getResourceAsString("aoc2025/12-main.txt");


    private static int getTotalTilesRequired(int[] state, int[] weights) {
        if (state.length != weights.length) {
            throw new IllegalArgumentException();
        }
        int total = 0;
        for (int idx = 0; idx < state.length; idx++) {
            total += weights[idx] * state[idx];
        }
        return total;
    }

    private static boolean isFieldTooBig(int w, int h, int[] state) {
        int num3x3 = (w / 3) * (h / 3);
        int numShapesTotal = Arrays.stream(state).sum();
        return num3x3 >= numShapesTotal;
    }

    public static void main(String[] args) {
        String input = INPUT;

        Instant start = Instant.now();

        String[] s0 = input.split("\n\n");

        List<String> shapes = Arrays.stream(s0, 0, s0.length - 1)
                .map(s -> s.split(":\n")[1])
                .toList();

        int[] weights = shapes.stream()
                .mapToInt(str -> StringUtils.countMatches(str, '#'))
                .toArray();

        String tasks = s0[s0.length - 1];

        AtomicLong cnt1 = new AtomicLong();

        // Sort tasks by "field size"
        tasks.lines().sorted().forEach(line -> {
            String[] s11 = line.split(": ");
            int[] state = Arrays.stream(s11[1].split(" "))
                    .mapToInt(Integer::parseUnsignedInt)
                    .toArray();
            String[] s111 = s11[0].split("x");
            int h = Integer.parseInt(s111[0]);
            int w = Integer.parseInt(s111[1]);

            int totalTilesRequired = getTotalTilesRequired(state, weights);
            boolean totalCheckOk = totalTilesRequired <= w * h;
            if (!totalCheckOk) {
                return;
            }

            boolean isFieldTooBig = isFieldTooBig(w, h, state);
            if (isFieldTooBig) {
                cnt1.getAndIncrement();
                return;
            }
            throw new IllegalArgumentException("Input is too hard!");
        });

        long result1 = cnt1.get();

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 476) {
                System.out.println("Wrong Result 1");
            }
        }
        System.out.println("Result (Part 1): " + cnt1);
    }
}

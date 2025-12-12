package aoc2023;

import org.apache.commons.lang3.StringUtils;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Day_02 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2023/02-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2023/02-main.txt");


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

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

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

        System.out.printf("Time: %d ms\n", Duration.between(start, Instant.now()).toMillis());

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 2563) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 70768) {
                System.out.println("Wrong Result 2");
            }
        }

        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

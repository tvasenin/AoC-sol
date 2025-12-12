package aoc2024;

import util.Resources;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Day_19 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/19-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/19-main.txt");


    private static long getNumWorkingDesigns(String design, List<String> towels, Map<String, Long> cache) {
        Long cachedValue = cache.get(design);
        if (cachedValue != null) {
            return cachedValue;
        }
        long numWorkingDesigns = 0;
        for (String towel : towels) {
            if (design.startsWith(towel)) {
                int idxNew = towel.length();
                if (idxNew == design.length()) {
                    numWorkingDesigns++;
                    continue;
                }
                String subDesign = design.substring(idxNew);
                long numSubDesigns = getNumWorkingDesigns(subDesign, towels, cache);
                numWorkingDesigns += numSubDesigns;
            }
        }
        cache.put(design, numWorkingDesigns);
        return numWorkingDesigns;
    }

    public static void main(String[] args) throws IOException {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        String[] s0 = input.split("\n\n");
        List<String> towels = Arrays.stream(s0[0].split(", ")).toList();
        List<String> designs = Arrays.stream(s0[1].split("\n")).toList();

        Map<String, Long> cache = new ConcurrentHashMap<>();
        designs.forEach(design -> cache.put(design, getNumWorkingDesigns(design, towels, cache)));

        long result1 = designs.stream().filter(design -> cache.get(design) > 0).count();
        long result2 = designs.stream().mapToLong(cache::get).sum();

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 216) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 603191454138773L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

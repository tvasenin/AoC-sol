package aoc2024;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Day_19 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/19-test.txt"), 6, 16);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/19-main.txt"), 216, 603191454138773L);


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

    public static TaskSolution solve(String input) {
        String[] s0 = input.split("\n\n");
        List<String> towels = Arrays.stream(s0[0].split(", ")).toList();
        List<String> designs = Arrays.stream(s0[1].split("\n")).toList();

        Map<String, Long> cache = new ConcurrentHashMap<>();
        designs.forEach(design -> cache.put(design, getNumWorkingDesigns(design, towels, cache)));

        long result1 = designs.stream().filter(design -> cache.get(design) > 0).count();
        long result2 = designs.stream().mapToLong(cache::get).sum();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_19::solve, false);
        Helpers.runTask(MAIN, Day_19::solve, false);
    }
}

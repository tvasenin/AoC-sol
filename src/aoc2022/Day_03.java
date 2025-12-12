package aoc2022;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Day_03 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2022/03-test.txt"), 157, 70);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2022/03-main.txt"), 8123, 2620);


    private static int getWeight(int c) {
        if ('a' <= c && c <= 'z') {
            return c - 'a' + 1;
        }
        if ('A' <= c && c <= 'Z') {
            return 26 + c - 'A' + 1;
        }
        throw new IllegalStateException("Unexpected value: " + c);
    }

    public static TaskSolution solve(String input) {
        long result1 = 0, result2 = 0;
        String[] lines = input.split("\n");
        for (String line : lines) {
            if (line.length() % 2 != 0) {
                throw new IllegalStateException("Unexpected line: " + line);
            }
            int size = line.length();
            int[] weights1 = line.substring(0, size / 2).chars().map(Day_03::getWeight).toArray();
            int[] weights2 = line.substring(size / 2, size).chars().map(Day_03::getWeight).toArray();
            Set<Integer> set1 = Arrays.stream(weights1).boxed().collect(Collectors.toSet());
            Set<Integer> set2 = Arrays.stream(weights2).boxed().collect(Collectors.toSet());
            Set<Integer> dupesLocal = new HashSet<>(set1);
            dupesLocal.retainAll(set2);
            if (dupesLocal.size() != 1) {
                throw new IllegalStateException("Unexpected dupes: " + dupesLocal);
            }
            result1 += dupesLocal.iterator().next();
        }
        if (lines.length % 3 != 0) {
            throw new IllegalStateException("Elves count not divisible by 3: " + lines.length);
        }
        int numGroups = lines.length / 3;
        for (int i = 0; i < numGroups; i++) {
            @SuppressWarnings("PointlessArithmeticExpression")
            Set<Integer> set1 = lines[i * 3 + 0].chars().map(Day_03::getWeight).boxed().collect(Collectors.toSet());
            Set<Integer> set2 = lines[i * 3 + 1].chars().map(Day_03::getWeight).boxed().collect(Collectors.toSet());
            Set<Integer> set3 = lines[i * 3 + 2].chars().map(Day_03::getWeight).boxed().collect(Collectors.toSet());
            Set<Integer> dupesLocal = new HashSet<>(set1);
            dupesLocal.retainAll(set2);
            dupesLocal.retainAll(set3);
            if (dupesLocal.size() != 1) {
                throw new IllegalStateException("Unexpected dupes: " + dupesLocal);
            }
            result2 += dupesLocal.iterator().next();
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_03::solve);
        Helpers.runTask(MAIN, Day_03::solve);
    }
}

package aoc2024;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.factory.SortedBags;
import org.eclipse.collections.api.list.ImmutableList;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day_01 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/01-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/01-main.txt");


    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        List<Integer> list1 = new ArrayList<>();
        MutableSortedBag<Integer> bag2 = SortedBags.mutable.empty();
        long result1 = 0, result2 = 0;

        Instant start = Instant.now();

        input.lines().forEach(line -> {
            String[] split = line.split("\\s+");
            list1.add(Integer.parseInt(split[0]));
            bag2.add(Integer.parseInt(split[1]));
        });
        Collections.sort(list1);
        ImmutableList<Integer> list2 = bag2.toImmutableSortedList();
        int n = list1.size();
        for (int i = 0; i < n; i++) {
            int first = list1.get(i);
            int second = list2.get(i);
            result1 += Math.abs(first - second);
            result2 += (long) first * bag2.occurrencesOf(first);
        }

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 1223326) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 21070419) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

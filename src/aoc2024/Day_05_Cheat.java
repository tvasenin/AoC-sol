package aoc2024;

import org.eclipse.collections.api.block.comparator.primitive.IntComparator;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import util.CollectionUtils;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day_05_Cheat {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/05-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/05-main.txt");


    @SuppressWarnings("unused")
    private static boolean isOk(IntList pages, Map<Integer, Set<Integer>> rules) {
        for (int i = 1; i < pages.size(); i++) {
            int page = pages.get(i);
            Set<Integer> pagesNotBefore = rules.getOrDefault(page, Collections.emptySet());
            if (pagesNotBefore.isEmpty()) {
                continue;
            }
            for (int j = 0; j < i; j++) {
                if (pagesNotBefore.contains(pages.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static IntComparator getPageOrderComparator(Map<Integer, Set<Integer>> rules) {
        return (p1, p2) -> {
            if (p1 == p2) {
                return 0;
            }
            boolean isOk1 = rules.getOrDefault(p1, Collections.emptySet()).contains(p2);
            boolean isOk2 = rules.getOrDefault(p2, Collections.emptySet()).contains(p1);
            if (isOk1 && isOk2) {
                throw new IllegalArgumentException(String.format("Both %s|%s and %s|%s rules found", p1, p2, p2, p1));
            }
            if (!isOk1 && !isOk2) {
                throw new IllegalArgumentException(String.format("Neither %s|%s or %s|%s rule found", p1, p2, p2, p1));
            }
            return isOk1 ? -1 : 1;
        };
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        long result1 = 0, result2 = 0;

        Map<Integer, Set<Integer>> rules = new HashMap<>();
        for (String line : input.split("\n")) {
            if (!line.isBlank()) {
                if (line.contains("|")) {
                    String[] s0 = line.split("\\|");
                    int pageBefore = Integer.parseInt(s0[0]);
                    int pageAfter = Integer.parseInt(s0[1]);
                    rules.computeIfAbsent(pageBefore, k -> new HashSet<>()).add(pageAfter);
                } else {
                    IntComparator comparator = getPageOrderComparator(rules);
                    MutableIntList pages = CollectionUtils.parseToMutableIntList(line, ",");
                    // This can be called cheating, as it's not guaranteed that the rules make full order for "correct"
                    // page sequences, but in fact they do!
                    // Uncomment the following line to perform the fully correct check:
//                    if (isOk(pages, rules)) {
                    if (CollectionUtils.isSorted(pages, comparator)) {
                        result1 += pages.get(pages.size() / 2);
                    } else {
                        // "Incorrect" sequences are explicitly guaranteed to be able to be unambiguously sorted
                        pages.sortThis(comparator);
                        result2 += pages.get(pages.size() / 2);
                    }
                }
            }
        }

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 5275) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 6191) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

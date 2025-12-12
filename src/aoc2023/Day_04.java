package aoc2023;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.factory.primitive.IntSets;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class Day_04 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2023/04-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2023/04-main.txt");


    static int[] parseIntegers(String input) {
        return Arrays.stream(input.split(" "))
                .mapToInt(Integer::parseUnsignedInt)
                .toArray();
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        // Warmup
        IntSets.mutable.empty();

        Instant start = Instant.now();
        List<String> lines = input.lines().toList();

        long result1 = 0, result2 = 0;

        int[] numCards = new int[lines.size()];
        Arrays.fill(numCards, 1);
        for (int i = 0; i < lines.size(); i++) {
            result2 += numCards[i];
            String line = lines.get(i);
            String[] s0 = StringUtils.split(line, ":|");
            // FIXME: count
            ImmutableIntSet winningNumbers = IntSets.immutable.of(parseIntegers(s0[1]));
            MutableIntSet matchingNumbers = IntSets.mutable.of(parseIntegers(s0[2]));
            matchingNumbers.retainAll(winningNumbers);
            if (!matchingNumbers.isEmpty()) {
                result1 += 1L << (matchingNumbers.size() - 1);
                int multiplier = numCards[i];
                int numWins = matchingNumbers.size();
                for (var j = i + 1; j < i + 1 + numWins; j++) {
                    numCards[j] += multiplier;
                }
            }
        }

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 20407) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 23806951) {
                System.out.println("Wrong Result 2");
            }
        }

        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

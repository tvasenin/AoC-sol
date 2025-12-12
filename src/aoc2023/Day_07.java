package aoc2023;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day_07 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/07-test.txt"), 6440, 5905);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/07-main.txt"), 241344943, 243101568);


    enum Rank {
        HIGH_KIND, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND,
    }

    static Rank getRank(String hand) {
        MultiSet<Character> counts = new HashMultiSet<>();
        hand.chars().forEach(c -> counts.add((char) c));
        int[] sortedCounts = counts.entrySet().stream().mapToInt(MultiSet.Entry::getCount).sorted().toArray();
        int length = sortedCounts.length;
        int highestCount = sortedCounts[sortedCounts.length - 1];
        if (counts.contains('0')) {
            if (length > 1) {
                // not all jokers
                length -= 1;
                switch (counts.getCount('0')) {
                    case 1: highestCount += 1; break;
                    case 2: highestCount = highestCount == 3 ? 5 : sortedCounts[sortedCounts.length - 2] + 2; break;
                    case 3: highestCount = sortedCounts[sortedCounts.length - 2] + 3; break;
                    case 4: return Rank.FIVE_OF_A_KIND;
                }
            }
        }

        return switch (length) {
            case 1 -> Rank.FIVE_OF_A_KIND;
            case 2 -> highestCount == 4 ? Rank.FOUR_OF_A_KIND : Rank.FULL_HOUSE;
            case 3 -> highestCount == 3 ? Rank.THREE_OF_A_KIND : Rank.TWO_PAIR;
            case 4 -> Rank.ONE_PAIR;
            case 5 -> Rank.HIGH_KIND;
            default -> throw new RuntimeException("Invalid size!");
        };
    }

    final static Comparator<String> cardsComparator = (cards1, cards2) -> {
        int c = getRank(cards1).compareTo(getRank(cards2));
        if (c == 0) {
            return cards1.compareTo(cards2);
        }
        return c;
    };

    final static Comparator<Hand> handsComparator = Comparator.comparing(hand -> hand.cards, cardsComparator);

    record Hand(String cards, int prize) { }

    public static TaskSolution solve(String input) {
        List<String> lines = input.lines().toList();

        long result1 = 0, result2 = 0;
        List<Hand> hands1 = new ArrayList<>(lines.size());
        List<Hand> hands2 = new ArrayList<>(lines.size());
        for (String line : lines) {
            String[] items = StringUtils.split(line);
            int prize = Integer.parseInt(items[1]);
            String orderedHand = items[0]
                    .replace('A', 'Z')
                    .replace('T', 'A')
                    .replace('J', 'B')
                    .replace('Q', 'C')
                    .replace('K', 'D')
                    .replace('Z', 'E');
            hands1.add(new Hand(orderedHand, prize));
            // Replace joker
            hands2.add(new Hand(orderedHand.replace('B', '0'), prize));
        }
        hands1.sort(handsComparator);
        hands2.sort(handsComparator);
        for (int i = 0; i < hands1.size(); i++) {
            result1 += (long) hands1.get(i).prize() * (i + 1);
        }
        for (int i = 0; i < hands2.size(); i++) {
            result2 += (long) hands2.get(i).prize() * (i + 1);
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_07::solve, false);
        Helpers.runTask(MAIN, Day_07::solve, false);
    }
}

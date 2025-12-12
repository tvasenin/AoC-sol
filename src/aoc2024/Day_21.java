package aoc2024;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import util.Cell;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Day_21 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/21-test.txt"), 126384, 154115708116294L);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/21-main.txt"), 94426, 118392478819140L);


    /*
        +---+---+---+
        | 7 | 8 | 9 |
        +---+---+---+
        | 4 | 5 | 6 |
        +---+---+---+
        | 1 | 2 | 3 |
        +---+---+---+
            | 0 | A |
            +---+---+


            +---+---+
            | ^ | A |
        +---+---+---+
        | < | v | > |
        +---+---+---+


            +---+---+
            | ^ | A |
        +---+---+---+
        | < | v | > |
        +---+---+---+



            +---+---+
            | ^ | A |
        +---+---+---+
        | < | v | > |
        +---+---+---+
     */

    private static final Map<String, String> KP_TO_KP = new HashMap<>();
    static {
        // same
        KP_TO_KP.put("AA", "A");
        KP_TO_KP.put("^^", "A");
        KP_TO_KP.put("<<", "A");
        KP_TO_KP.put("vv", "A");
        KP_TO_KP.put(">>", "A");

        // 1 up
        KP_TO_KP.put("v^", "^A");
        KP_TO_KP.put(">A", "^A");

        // 1 left
        KP_TO_KP.put("A^", "<A");
        KP_TO_KP.put("v<", "<A");
        KP_TO_KP.put(">v", "<A");

        // 2 left
        KP_TO_KP.put("><", "<<A");

        // 1 down
        KP_TO_KP.put("^v", "vA");
        KP_TO_KP.put("A>", "vA");

        // 1 right
        KP_TO_KP.put("^A", ">A");
        KP_TO_KP.put("<v", ">A");
        KP_TO_KP.put("v>", ">A");

        // 2 right
        KP_TO_KP.put("<>", ">>A");

        KP_TO_KP.put("Av", "<vA"); // optimal
        KP_TO_KP.put("^>", "v>A"); // optimal
        KP_TO_KP.put("vA", "^>A"); // optimal
        KP_TO_KP.put(">^", "<^A"); // optimal

        // Can't go through hole:
        KP_TO_KP.put("^<", "v<A"); // single option
        KP_TO_KP.put("<^", ">^A"); // single option
        KP_TO_KP.put("A<", "v<<A"); // optimal
        KP_TO_KP.put("<A", ">>^A"); // optimal
    }


    /*
        +---+---+---+
        | 7 | 8 | 9 |
        +---+---+---+
        | 4 | 5 | 6 |
        +---+---+---+
        | 1 | 2 | 3 |
        +---+---+---+
            | 0 | A |
            +---+---+
     */

    private static final Map<String, String> NP_TO_KP = new HashMap<>();
    static {
        Instant start = Instant.now();

        // Calculate the remaining keys
        char[] keys = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A' };
        for (char keyFrom : keys) {
            for (char keyTo : keys) {
                NP_TO_KP.put("" + keyFrom + keyTo, getBestSequence(keyFrom, keyTo));
            }
        }

        // Validate calculated samples (found in inputs)
        assert NP_TO_KP.get("1A").equals(">>vA");
        assert NP_TO_KP.get("A1").equals("^<<A");
        assert NP_TO_KP.get("29").equals("^^>A");
        assert NP_TO_KP.get("34").equals("<<^A");
        assert NP_TO_KP.get("37").equals("<<^^A");
        assert NP_TO_KP.get("40").equals(">vvA");
        assert NP_TO_KP.get("5A").equals("vv>A");
        assert NP_TO_KP.get("70").equals(">vvvA");
        assert NP_TO_KP.get("83").equals("vv>A");
        assert NP_TO_KP.get("A2").equals("<^A");
        assert NP_TO_KP.get("A4").equals("^^<<A");
        assert NP_TO_KP.get("A5").equals("<^^A");
        assert NP_TO_KP.get("A8").equals("<^^^A");

        System.out.println("Precalc: " + Duration.between(start, Instant.now()).toMillis() + "ms");
    }

    private static String getBestSequence(char keyFrom, char keyTo) {
        Cell cell1 = getNumpadCell(keyFrom);
        Cell cell2 = getNumpadCell(keyTo);
        String seq = getCanonicalSequence(cell1, cell2);
        boolean isRestricted = isRestricted(cell1, cell2);
        return findBestPermutation(seq, isRestricted ? seq : null);
    }

    private static Cell getNumpadCell(char key) {
        return switch (key) {
            case 'A' -> new Cell(3, 2);
            case '0' -> new Cell(3, 1);
            case '1' -> new Cell(2, 0);
            case '2' -> new Cell(2, 1);
            case '3' -> new Cell(2, 2);
            case '4' -> new Cell(1, 0);
            case '5' -> new Cell(1, 1);
            case '6' -> new Cell(1, 2);
            case '7' -> new Cell(0, 0);
            case '8' -> new Cell(0, 1);
            case '9' -> new Cell(0, 2);
            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }

    private static String getCanonicalSequence(Cell keyFrom, Cell keyTo) {
        int dRow = keyTo.row() - keyFrom.row();
        int dCol = keyTo.col() - keyFrom.col();
        String sRow = (dRow > 0 ? "v" : "^").repeat(Math.abs(dRow));
        String sCol = (dCol > 0 ? ">" : "<").repeat(Math.abs(dCol));
        // Ensure left/down are before right/up, in order to use it as a forbidden move if needed
        return (dCol > 0) ? (sRow + sCol + "A") : (sCol + sRow + "A");
    }

    private static boolean isRestricted(Cell keyFrom, Cell keyTo) {
        return (keyFrom.row() == 3 && keyTo.col() == 0) || (keyTo.row() == 3 && keyFrom.col() == 0);
    }

    private static List<Character> toCharacterList(String string) {
        return string.chars().mapToObj(s -> (char) s).collect(Collectors.toList());
    }

    private static String toString(Collection<Character> collection) {
        return collection.stream().map(Object::toString).collect(Collectors.joining());
    }

    private static Set<String> getAllValidCombinations(String moves, String forbidden) {
        // Remove final 'A' before permuting
        List<Character> characters = toCharacterList(moves.substring(0, moves.length() - 1));
        return CollectionUtils.permutations(characters)
                .stream()
                .map(chars -> toString(chars) + "A")
                .filter(s -> !s.equals(forbidden))
                .collect(Collectors.toSet());
    }

    private static String findBestPermutation(String s, String forbidden) {
        if (!s.endsWith("A")) throw new AssertionError();
        if (s.length() < 3) return s;

        int numTranslations = 8; // Should be enough for all cases
        Set<String> combinations = getAllValidCombinations(s, forbidden);

        int bestCnt = 0;
        long minWeight = Long.MAX_VALUE;
        String bestPermutation = null;
        for (String combination : combinations) {
            long weight = getKpSequenceWeightSimple(combination, numTranslations);
            if (weight < minWeight) {
                bestPermutation = combination;
                minWeight = weight;
                bestCnt = 1;
            } else if (weight == minWeight) {
                bestCnt++;
            }
        }
        if (bestCnt > 1) {
            throw new RuntimeException("Some combinations have equal weight for " + s);
        }
//        System.out.println(s + ": " + bestPermutation);
        return bestPermutation;
    }

    private static long getKpSequenceWeightSimple(String moves, int numTranslations) {
        for (int i = 0; i < numTranslations; i++) {
            moves = translateKpToKpSimple(moves, KP_TO_KP);
        }
        return moves.length();
    }

    private static String translateKpToKpSimple(String s,
                                                @SuppressWarnings("SameParameterValue") Map<String, String> lut) {
        // Starting and ending with A
        String moves = "A" + s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moves.length() - 1; i++) {
            String curMove = moves.substring(i, i + 2);
            String translatedMove = lut.get(curMove);
            if (translatedMove == null) {
                throw new IllegalArgumentException("Invalid move: " + curMove);
            }
            sb.append(translatedMove);
        }
        return sb.toString();
    }

    /*
        +---+---+---+
        | 7 | 8 | 9 |
        +---+---+---+
        | 4 | 5 | 6 |
        +---+---+---+
        | 1 | 2 | 3 |
        +---+---+---+
            | 0 | A |
            +---+---+
     */


    private static ObjectLongMap<String> translate(ObjectLongMap<String> moves, Map<String, String> lut) {
        MutableObjectLongMap<String> newMoves = ObjectLongMaps.mutable.empty();
        moves.forEachKeyValue((move, count) -> {
            String moves2 = "A" + move;
            for (int i = 0; i < moves2.length() - 1; i++) {
                String curMove = moves2.substring(i, i + 2);
                String translatedMove = lut.get(curMove);
                if (translatedMove == null) {
                    throw new IllegalArgumentException("Invalid move: " + curMove);
                }
                newMoves.addToValue(translatedMove, count);
            }
        });
        return newMoves;
    }

    private static long getNumMoves(String code, int numKeypads) {
        ObjectLongMap<String> moves = ObjectLongMaps.mutable.of(code, 1);
        moves = translate(moves, NP_TO_KP);
        for (int i = 0; i < numKeypads - 1; i++) {
            moves = translate(moves, KP_TO_KP);
        }
        AtomicLong numMoves = new AtomicLong();
        moves.forEachKeyValue((move, count) -> numMoves.addAndGet((long) move.length() * count));
        return numMoves.get();
    }

    public static TaskSolution solve(String input) {
        long result1 = 0;
        long result2 = 0;
        for (String code : input.split("\n")) {
            int value = Integer.parseInt(code.substring(0, 3));
            result1 += value * getNumMoves(code, 3);
            result2 += value * getNumMoves(code, 26);
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        ObjectLongMaps.mutable.empty(); // Warmup
        Helpers.runTask(TEST, Day_21::solve, true);
        Helpers.runTask(MAIN, Day_21::solve, true);
    }
}

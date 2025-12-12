package aoc2025;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day_10_Parity {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/10-test.txt"), 7, 33);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/10-main.txt"), 411, 16063);


    private record MachineDescription(BitSet state, List<int[]> switches, int[] joltages) {
        public static MachineDescription parse(String line) {
            List<String> s0 = Arrays.stream(line.split(" ")).toList();
            BitSet state = ArrayUtils.indexesOf(
                    StringUtils.substringBetween(s0.getFirst(), "[", "]").toCharArray(),
                    '#'
            );
            int[] joltages = Helpers.parseCommaDelimitedIntArray(
                    StringUtils.substringBetween(s0.getLast(), "{", "}")
            );
            List<int[]> switches = s0.subList(1, s0.size() - 1).stream()
                    .map(s -> StringUtils.substringBetween(s, "(",  ")"))
                    .map(Helpers::parseCommaDelimitedIntArray)
                    .toList();
            return new MachineDescription(state, switches, joltages);
        }
    }

    private static void doIncrement(int[] state, List<int[]> switches) {
        for (int[] sw : switches) {
            for (int idx : sw) {
                state[idx]++;
            }
        }
    }

    private static boolean doDecrement(int[] state, List<int[]> switches) {
        boolean success = true;
        for (int[] sw : switches) {
            for (int idx : sw) {
                if (--state[idx] < 0) {
                    success = false;
                }
            }
        }
        return success;
    }

    private static BitSet getParityMask(int[] state) {
        BitSet mask = new BitSet(state.length);
        for (int idx = 0; idx < state.length; idx++) {
            int val = state[idx];
            if (val % 2 == 1) {
                mask.set(idx);
            }
        }
        return mask;
    }

    private static Map<BitSet, Set<BitSet>> getParityMap(List<int[]> switches, int stateLength) {
        Map<BitSet, Set<BitSet>> parityCombinations = new HashMap<>();
        int numSwitches = switches.size();
        long allMasks = 1L << numSwitches;
        for (int i = 0; i < allMasks; i++) {
            BitSet combination = new BitSet(numSwitches);
            BitSet stateParity =  new BitSet(stateLength);
            // Include empty set as well!
            for (int j = 0; j < numSwitches; j++) {
                if ((i & (1 << j)) > 0) { //The j-th element is used
                    combination.set(j);
                    int[] sw = switches.get(j);
                    for (int idx : sw) {
                        stateParity.flip(idx);
                    }
                }
            }
            parityCombinations.computeIfAbsent(stateParity, k -> new HashSet<>()).add(combination);
        }
        return parityCombinations;
    }

    private static boolean allZeros(int[] state) {
        for (int i : state) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    private static void doShrState(int[] state) {
        for (int i = 0; i < state.length; i++) {
            state[i] >>= 1;
        }
    }

    private static void doShlState(int[] state) {
        for (int i = 0; i < state.length; i++) {
            state[i] <<= 1;
        }
    }

    private static int getMinNumPresses2(int[] state, List<int[]> switches, Map<BitSet, Set<BitSet>> parityMap) {
        if (allZeros(state)) {
            return 0;
        }

        int numPresses = Integer.MAX_VALUE;
        Set<BitSet> variants = parityMap.getOrDefault(getParityMask(state), Collections.emptySet());
        for (BitSet variant : variants) {
            List<int[]> swVariant = variant.stream().mapToObj(switches::get).toList();
            boolean success = doDecrement(state, swVariant);
            if (success) {
                doShrState(state);
                int curNumPresses = getMinNumPresses2(state, switches, parityMap);
                if (curNumPresses != -1) {
                    curNumPresses *= 2;
                    // Add number of the pressed switches
                    curNumPresses += swVariant.size();
                    numPresses = Math.min(numPresses, curNumPresses);
                }
                // Cleanup
                doShlState(state);
            }
            // Cleanup
            doIncrement(state, swVariant);
        }

        if (numPresses == Integer.MAX_VALUE) {
            numPresses = -1;
        }

        return numPresses;
    }

    public static TaskSolution solve(String input) {
        List<MachineDescription> machines = input.lines()
                .map(MachineDescription::parse)
                .toList();

        long result1 = 0;
        long result2 = 0;
        for (MachineDescription machine : machines) {
            BitSet state = machine.state;
            List<int[]> switches = machine.switches;
            int[] joltages = machine.joltages;
            Map<BitSet, Set<BitSet>> parityMap = getParityMap(switches, joltages.length);
            result1 += parityMap.get(state).stream().mapToLong(BitSet::cardinality).min().orElseThrow();
            result2 += getMinNumPresses2(Arrays.copyOf(joltages, joltages.length), switches, parityMap);
        }

        return TaskSolution.of(result1, result2);
    }


    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_10_Parity::solve, true);
        Helpers.runTask(MAIN, Day_10_Parity::solve, false);
    }
}

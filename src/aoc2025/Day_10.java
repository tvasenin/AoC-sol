package aoc2025;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Day_10 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2025/10-test.txt"), 7, 33);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/10-main.txt"), 411, 16063);


    private record MachineDescription(String state, List<int[]> switches, int[] joltages) {
        public static MachineDescription parse(String line) {
            List<String> s0 = Arrays.stream(line.split(" ")).toList();
            String state = StringUtils.substringBetween(s0.getFirst(), "[", "]");
            int[] joltages = Helpers.parseCommaDelimitedIntArray(
                    StringUtils.substringBetween(s0.getLast(), "{", "}")
            );
            List<int[]> switches = s0.subList(1, s0.size() - 1).stream()
                    .map(s -> StringUtils.substringBetween(s, "(",  ")"))
                    .map(Helpers::parseCommaDelimitedIntArray)
                    .toList();
            return new MachineDescription(state, switches, joltages);
        }

        @Override
        public String toString() {
            String stateString = "[" + state + "]";
            String switchesString = switches.stream()
                    .map(sw -> Arrays.toString(sw)
                            .replace(" ", "")
                            .replace('[', '(')
                            .replace(']', ')')
                    )
                    .collect(Collectors.joining(" "));

            String joltagesString = Arrays.toString(joltages)
                    .replace(" ", "")
                    .replace('[', '{')
                    .replace(']', '}');

            return String.join(" ", stateString, switchesString, joltagesString);
        }
    }

    private static long getMinNumPresses1(MachineDescription machine) {
        List<int[]> switches = machine.switches();

        // Make initial state to be the target state, so the new target state would be empty
        BitSet state = ArrayUtils.indexesOf(machine.state().toCharArray(), '#');

        return getMinNumPresses1(state, switches);
    }

    private static long getMinNumPresses1(BitSet state, List<int[]> switches) {
        if (state.isEmpty()) {
            return 0;
        }

        if (switches.isEmpty()) {
            return -1;
        }

        int[] firstSwitch = switches.getFirst();
        List<int[]> remainingSwitches = switches.subList(1, switches.size());

        // Try press the first switch
        Arrays.stream(firstSwitch).forEach(state::flip);
        long numPressesWith = getMinNumPresses1(state, remainingSwitches);
        if (numPressesWith != -1) {
            // Add pressing of the first switch
            numPressesWith++;
        }
        // Cleanup
        Arrays.stream(firstSwitch).forEach(state::flip);

        long numPressesWithout = getMinNumPresses1(state, remainingSwitches);

        if (numPressesWith == -1 || numPressesWithout == -1) {
            return numPressesWith == -1 ? numPressesWithout : numPressesWith;
        }
        return Math.min(numPressesWith, numPressesWithout);
    }

    private static int getMaxNumPresses(int[] state, int[] sw) {
        int result = Integer.MAX_VALUE;
        for (int idx : sw) {
            result = Math.min(result, state[idx]);
        }
        return result;
    }

    private static void doIncrement(int[] state, int[] sw) {
        for (int idx : sw) {
            state[idx]++;
        }
    }

    private static void doDecrement(int[] state, int[] sw) {
        for (int idx : sw) {
            state[idx]--;
        }
    }

    private static void doDecrementTimes(int[] state, int[] sw, int times) {
        for (int idx : sw) {
            state[idx] -= times;
        }
    }

    private static int[] getMask(List<int[]> switches, int length) {
        int[] mask = new int[length];
        for (int[] sw : switches) {
            for (int idx : sw) {
                mask[idx]++;
            }
        }
        return mask;
    }

    private static int getMinNumPresses2(MachineDescription machine) {
        List<int[]> switches = machine.switches().stream()
                .sorted(Comparator.comparingInt(Array::getLength).reversed())
                .toList();

        int[] state = machine.joltages;

        int[] mask = getMask(switches, state.length);
        return getMinNumPresses2(state, switches, 0, mask);
    }

    private static int canContinueMask(int[] state, int numRemainingSwitches, int[] mask) {
        boolean allzero = true;
        boolean allAffectNonZeroCell = false;
        for (int idx = 0; idx < state.length; idx++) {
            int maskVal = mask[idx];
            if (state[idx] != 0) {
                allzero = false;
                if (maskVal == 0) {
                    // No switches affect non-zero cell
                    return -1;
                }
            } else {
                if (maskVal > 0 && maskVal == numRemainingSwitches) {
                    // All switches affect zero cell
                    if (!allzero) {
                        return -1;
                    } else {
                        allAffectNonZeroCell = true;
                    }
                }
            }
        }
        return allzero ? 0 : allAffectNonZeroCell ? -1 : 1;
    }

    private static int getMinNumPresses2(int[] state, List<int[]> switches, int idxSwitch, int[] mask) {
        int numRemainingSwitches = switches.size() - idxSwitch;

        int ccm = canContinueMask(state, numRemainingSwitches, mask);
        if (ccm <= 0) {
            return ccm;
        }

        int[] sw = switches.get(idxSwitch);
        idxSwitch++;
        doDecrement(mask, sw);

        int numPresses = Integer.MAX_VALUE;

        int maxNumPresses = getMaxNumPresses(state, sw);
        doDecrementTimes(state, sw, maxNumPresses + 1);
        for (int times = maxNumPresses; times >= 0; times--) {
            doIncrement(state, sw);
            int curNumPresses = getMinNumPresses2(state, switches, idxSwitch, mask);
            if (curNumPresses != -1) {
                // Add pressings of the first switch
                curNumPresses += times;
                if (curNumPresses < numPresses) {
                    numPresses = curNumPresses;
                }
            }
        }

        // Cleanup
        doIncrement(mask, sw);

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
        for (MachineDescription machine : machines) {
            result1 += getMinNumPresses1(machine);
        }

        AtomicLong cnt = new AtomicLong();
        try (ExecutorService executor = Executors.newFixedThreadPool(machines.size())) {
            for (MachineDescription machine : machines) {
                executor.submit(() -> {
                    int numPasses2 = getMinNumPresses2(machine);
                    cnt.addAndGet(getMinNumPresses2(machine));
                    synchronized (System.out) {
                        System.out.println(numPasses2 + "\t" + machine);
                        System.out.flush();
                    }
                });
            }
            executor.shutdown();
            try {
                boolean ok = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                if (!ok) {
                    throw new RuntimeException();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long result2 = cnt.get();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_10::solve, true);
        Helpers.runTask(MAIN, Day_10::solve, false);
    }
}

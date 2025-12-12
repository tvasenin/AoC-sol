package aoc2023;

import one.util.streamex.EntryStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Day_08 {

    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/08-main.txt"), 13939, 8906539031197L);


    private record Node(int l, int r) { }

    private record ExtendedGCD(long gcd, long x, long y) { }

    private record CycleInfo(long offset, long period, ImmutableLongList terminalIndices) { }

    private record StepState(int nodeId, int stepIdx) { }

    private static final CycleInfo IDENTITY_CYCLE = new CycleInfo(0, 1, LongLists.immutable.of(0));

    private static CycleInfo getCycle(Node[] nodes, char[] steps, int startId, int endId) {
        return getCycle(nodes, steps, startId, new int[] { endId });
    }

    private static CycleInfo getCycle(Node[] nodes, char[] steps, int startId, int[] endIds) {
        long curStep = 0;
        int currId = startId;
        int pos = 0;
        Map<StepState, Long> states = new HashMap<>(nodes.length * steps.length * 2);
        MutableLongList terminalIndices = LongLists.mutable.empty();
        while (true) {
            for (int stepIdx = 0; stepIdx < steps.length; stepIdx++) {
                char step = steps[stepIdx];
                StepState state = new StepState(currId, stepIdx);
                if (states.containsKey(state)) {
                    // Cycle found
                    // Maybe attempt to reduce cycle here?
                    long cycleOffset = states.get(state);
                    long cycleLength = curStep - cycleOffset;

                    return new CycleInfo(cycleOffset, cycleLength, terminalIndices.toImmutable());
                }
                states.put(state, curStep);
                if (ArrayUtils.contains(endIds, state.nodeId)) {
                    terminalIndices.add(curStep);
                }

                // Advance
                Node currNode = nodes[currId];
                currId = switch (step) {
                    case 'L' -> currNode.l;
                    case 'R' -> currNode.r;
                    default -> throw new RuntimeException("Unexpected char!");
                };
                pos = (pos + 1) % steps.length;
                curStep++;
            }
        }
    }

    private static ExtendedGCD extendedGCD(long a, long b) {
        if (b == 0) {
            return new ExtendedGCD(a, 1L, 0L);
        }
        ExtendedGCD sol = extendedGCD(b, a % b);
        long x = sol.x;
        long y = sol.y;
        long q = a / b;
        //noinspection SuspiciousNameCombination
        return new ExtendedGCD(sol.gcd, y, x - q * y);
    }

    private static CycleInfo mergeCycles(CycleInfo cycle1, CycleInfo cycle2) {
        long newOffset = Math.max(cycle1.offset, cycle2.offset);
        long period1 = cycle1.period;
        long period2 = cycle2.period;
        ExtendedGCD eGCD = extendedGCD(period1, period2);
        long newPeriod = (period1 / eGCD.gcd) * (period2 / eGCD.gcd); // LCM
        MutableLongList newTerminalIndicesList = LongLists.mutable.empty();
        for (int i = 0; i < cycle1.terminalIndices.size(); i++) {
            for (int j = 0; j < cycle2.terminalIndices.size(); j++) {
                long newIdx = mergeIndices(cycle1, cycle2, i, j, eGCD);
                if (newIdx == -1) {
                    continue;
                }
                newTerminalIndicesList.add(newIdx);
            }
        }
        newTerminalIndicesList.sortThis();
        if (newTerminalIndicesList.isEmpty()) {
            throw new RuntimeException("Cycles have no common terminal states");
        }
        return new CycleInfo(newOffset, newPeriod, newTerminalIndicesList.toImmutable());
    }

    private static long mergeIndices(CycleInfo cycle1, CycleInfo cycle2, int i, int j, ExtendedGCD eGCD) {
        final long step1 = cycle1.terminalIndices.get(i);
        final long step2 = cycle2.terminalIndices.get(j);

        if (step1 == step2) {
            return step1;
        }

        // Try to catch with other period
        boolean isBeforeCycle1 = step1 < cycle1.offset;
        boolean isBeforeCycle2 = step2 < cycle2.offset;
        if (isBeforeCycle1 && isBeforeCycle2) {
            return -1;
        }
        long period1 = cycle1.period;
        long period2 = cycle2.period;
        if (isBeforeCycle1) {
            long diff = step1 - step2;
            if (diff > 0 && (period2 % diff == 0)) {
                return step1;
            }
            return -1;
        }
        if (isBeforeCycle2) {
            long diff = step2 - step1;
            if (diff > 0 && (period1 % diff == 0)) {
                return step2;
            }
            return -1;
        }

        return findMinMergedStep(step1, step2, period1, period2, eGCD);
    }

    private static long findMinMergedStep(long step1, long step2, long period1, long period2, ExtendedGCD eGCD) {
        // Solve linear Diophantine equation:
        // step1 + (period1 * x) = step2 + (period2 * y)
        // (period1 * x) - (period2 * y) = step2 - step1
        // ax + by = c
        // Need minimal positive x
        long c = step2 - step1;

        long gcd = eGCD.gcd;

        // Check solvability
        if (c % gcd != 0) {
            return -1;
        }

        // Use precalculated GCD
        long x = eGCD.x;
        long x0 = x * c / gcd;

        if (x0 < 0) {
            // Find first positive solution
            x0 = Math.floorMod(x0, period2 / gcd);
        }

        return step1 + period1 * x0;
    }

    public static TaskSolution solve(String input) {
        String[] sections = input.split("\n\n");

        char[] steps = sections[0].toCharArray();
        String nodesStr = sections[1];

        Pattern pattern = Pattern.compile("\\W+");

        List<String> names = nodesStr.lines().map(line -> StringUtils.substringBefore(line, " =")) .toList();

        Node[] nodes = nodesStr.lines()
                .map(pattern::split)
                .sorted(Comparator.comparingInt(s0 -> names.indexOf(s0[0])))
                .map(s0 -> new Node(names.indexOf(s0[1]), names.indexOf(s0[2])))
                .toArray(Node[]::new);

        long result1 = getCycle(nodes, steps, names.indexOf("AAA"), names.indexOf("ZZZ")).terminalIndices.getFirst();

        int[] startIds = EntryStream.of(names).filterValues(s -> s.endsWith("A")).mapToInt(Map.Entry::getKey).toArray();
        int[] endIds = EntryStream.of(names).filterValues(s -> s.endsWith("Z")).mapToInt(Map.Entry::getKey).toArray();

        List<CycleInfo> cycles = Arrays.stream(startIds).mapToObj(id -> getCycle(nodes, steps, id, endIds)).toList();
        CycleInfo mergedCycle = cycles.stream().reduce(IDENTITY_CYCLE, Day_08::mergeCycles);

        long result2 = mergedCycle.terminalIndices.getFirst();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        LongLists.mutable.empty(); // Warmup
        solve(
                """
                LLR

                AAA = (BBB, BBB)
                BBB = (CCC, CCC)
                CCC = (ZZZ, ZZZ)
                ZZZ = (BBB, BBB)
                """
        ); // Warmup
        Helpers.runTask(MAIN, Day_08::solve, false);
    }
}

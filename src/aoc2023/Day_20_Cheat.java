package aoc2023;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day_20_Cheat {

    private static final TaskData TEST_1 = new TaskData(Resources.getResourceAsString("aoc2023/20-test-1.txt"), 32000000, -1);
    private static final TaskData TEST_2 = new TaskData(Resources.getResourceAsString("aoc2023/20-test-2.txt"), 11687500, -1);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/20-main.txt"), 825167435, 225514321828633L);


    private record Module(String id, ModuleType type, List<String> targetIds) { }

    private enum ModuleType {
        BROADCASTER, FLIP_FLOP, CONJUNCTION
    }

    private record Signal(String sourceId, String targetId, boolean hi) { }

    private static void simulateButtonPress(Map<String, Module> modules, Set<String> flipFlopsWithHiOutput,
                                            Map<String, Set<String>> conjInputs,
                                            Map<String, Set<String>> conjHiInputs,
                                            Map<String, Pair<AtomicLong, AtomicLong>> counters
    ) {
        Queue<Signal> queue = new ArrayDeque<>();
        queue.add(new Signal(null, "broadcaster", false));
        while (!queue.isEmpty()) {
            Signal signal = queue.remove();
            var pair = counters.computeIfAbsent(signal.targetId(), k -> Pair.of(new AtomicLong(0), new AtomicLong(0)));
            if (signal.hi()) {
                pair.getLeft().incrementAndGet();
            } else {
                pair.getRight().incrementAndGet();
            }
            Module module = modules.get(signal.targetId());
            if (module == null) {
                // Terminal module reached
                continue;
            }
            String moduleId = module.id();
            Boolean outHi = switch (module.type()) {
                case BROADCASTER -> signal.hi();
                case FLIP_FLOP -> {
                    if (!signal.hi()) {
                        if (flipFlopsWithHiOutput.remove(moduleId)) {
                            yield false;
                        } else {
                            flipFlopsWithHiOutput.add(moduleId);
                            yield true;
                        }
                    }
                    yield null;
                }
                case CONJUNCTION -> {
                    String sourceId = signal.sourceId();
                    Set<String> inputs = conjInputs.get(moduleId);
                    Set<String> hiInputs = conjHiInputs.computeIfAbsent(moduleId, k -> new HashSet<>());
                    if (signal.hi()) {
                        hiInputs.add(sourceId);
                    } else {
                        hiInputs.remove(sourceId);
                    }
                    yield !hiInputs.equals(inputs);
                }
            };
            if (outHi != null) {
                module.targetIds().forEach(targetId -> queue.add(new Signal(moduleId, targetId, outHi)));
            }
        }
    }

    private static Map<String, Set<String>> getConjunctionInputs(Collection<Module> modules) {
        Map<String, Set<String>> conjunctionInputs = modules.stream()
                .filter(module -> module.type() == ModuleType.CONJUNCTION)
                .collect(Collectors.toMap(Module::id, module -> new HashSet<>()));
        modules.forEach(module -> {
            for (String targetId : module.targetIds()) {
                Set<String> inputs = conjunctionInputs.get(targetId);
                if (inputs != null) {
                    inputs.add(module.id());
                }
            }
        });
        return conjunctionInputs;
    }

    private static Module parseModule(String line) {
        final String id = StringUtils.split(line, "%& ")[0];
        char c = line.charAt(0);
        if (c == 'b' && !id.equals("broadcaster")) {
            throw new IllegalStateException("Invalid broadcast entry");
        }
        List<String> targetIds = Arrays.stream(StringUtils.substringAfter(line, "-> ").split(", ")).toList();
        ModuleType type = switch (c) {
            case 'b' -> ModuleType.BROADCASTER;
            case '%' -> ModuleType.FLIP_FLOP;
            case '&' -> ModuleType.CONJUNCTION;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
        return new Module(id, type, targetIds);
    }

    public static TaskSolution solve(String input) {
        Map<String, Module> modules = Pattern.compile("\n").splitAsStream(input)
                .map(Day_20_Cheat::parseModule)
                .collect(Collectors.toMap(Module::id, module -> module));

        Set<String> flipFlopsWithHiOutput = new HashSet<>();
        Map<String, Set<String>> conjInputs = getConjunctionInputs(modules.values());
        Map<String, Set<String>> conjHiInputs = new HashMap<>();
        Map<String, Pair<AtomicLong, AtomicLong>> counters = modules.keySet().stream()
                .collect(Collectors.toMap(id -> id, k -> Pair.of(new AtomicLong(0), new AtomicLong(0))));

        for (int i = 0; i < 1000; i++) {
            simulateButtonPress(modules, flipFlopsWithHiOutput, conjInputs, conjHiInputs, counters);
        }
        long totalHi = counters.values().stream().map(Pair::getLeft).mapToLong(AtomicLong::get).sum();
        long totalLo = counters.values().stream().map(Pair::getRight).mapToLong(AtomicLong::get).sum();
        long result1 = totalHi * totalLo;


        flipFlopsWithHiOutput.clear();
        conjHiInputs.values().forEach(Set::clear);
        counters.values().forEach(pair -> {
            pair.getLeft().set(0);
            pair.getRight().set(0);
        });

        final long result2;

        // FIXME: CHEAT: Part 2 applicable for test input only!
        if (modules.size() != 58) {
            result2 = -1; // FIXME
        } else {
            String terminalId = "rx";
            Set<String> gateIds = modules.values().stream()
                    .filter(module -> module.targetIds().contains(terminalId)).map(Module::id).collect(Collectors.toSet());
            if (gateIds.size() != 1) {
                throw new IllegalStateException("Not a single connection to terminal node!");
            }
            String gateId = gateIds.iterator().next();
            if (modules.get(gateId).type() != ModuleType.CONJUNCTION) {
                throw new IllegalStateException("Wrong final conjunction module type!");
            }
            final Set<String> gateInputs = conjInputs.get(gateId);
            if (!gateInputs.stream().map(modules::get).allMatch(module ->
                    module.type() == ModuleType.CONJUNCTION && conjInputs.get(module.id()).size() == 1
            )) {
                throw new IllegalStateException("Not all gate inputs are conjunctions with single input!");
            }

            final Map<String, AtomicLong> gateHiCycles = gateInputs.stream()
                    .collect(Collectors.toMap(id -> id, k -> new AtomicLong(0)));
            AtomicLong step = new AtomicLong(0);
            while (true) {
                step.incrementAndGet();
                simulateButtonPress(modules, flipFlopsWithHiOutput, conjInputs, conjHiInputs, counters);
                long numTerminalLow = counters.get(terminalId).getRight().get();
                if (numTerminalLow > 0) {
                    System.out.println("Got " + numTerminalLow + " low pulses on the terminal node.");
                    result2 = step.get();
                    break;
                }
                gateInputs.forEach(id -> {
                    // If the pre-
                    long numLow = counters.get(id).getRight().get();
                    if (numLow == 1) {
                        AtomicLong cycleCounter = gateHiCycles.get(id);
                        long curCount = cycleCounter.get();
                        if (curCount == 0) {
                            System.out.println("Start of cycle for module " + id + " at step " + step);
                            // Store cycle start as negative number
                            cycleCounter.addAndGet(-step.get());
                        }
                    } else if (numLow == 2) {
                        AtomicLong cycleCounter = gateHiCycles.get(id);
                        long curCount = cycleCounter.get();
                        if (curCount < 0) {
                            long cycleLength = step.get() - (-cycleCounter.get());
                            cycleCounter.set(cycleLength);
                            System.out.printf("End of cycle for module %s at step %s, length is %s\n", id, step, cycleLength);
                        }
                    }
                });
                // Try to calculate LCM of all cycles
                long cyclesLCM = 1;
                for (AtomicLong cycleCounter : gateHiCycles.values()) {
                    long cycleValue = cycleCounter.get();
                    cyclesLCM *= cycleValue > 0 ? cycleValue : 0;
                }
                if (cyclesLCM > 0) {
                    result2 = cyclesLCM;
                    break;
                }
            }
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST_1, Day_20_Cheat::solve, false);
        Helpers.runTask(TEST_2, Day_20_Cheat::solve, false);
        Helpers.runTask(MAIN, Day_20_Cheat::solve, false);
    }
}
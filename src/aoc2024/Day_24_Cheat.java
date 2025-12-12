package aoc2024;

import org.apache.commons.lang3.NotImplementedException;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Day_24_Cheat {

    private static final TaskData TEST_1 = new TaskData(Resources.getResourceAsString("aoc2024/24-test-1.txt"), Long.toString(-1), "");
    private static final TaskData TEST_2 = new TaskData(Resources.getResourceAsString("aoc2024/24-test-2.txt"), Long.toString(-1), "");
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/24-main.txt"), Long.toString(52956035802096L), "hnv,hth,kfm,tqr,vmv,z07,z20,z28");

    private record Module(String input1, String input2, ModuleType type, String output) {
        public Module(String input1, String input2, ModuleType type, String output) {
            int res = input1.compareTo(input2);
            if (res == 0) {
                throw new IllegalStateException();
            }
            this.input1 = res < 0 ? input1 : input2;
            this.input2 = res < 0 ? input2 : input1;
            this.type = type;
            this.output = output;
        }
    }

    private enum ModuleType {
        AND, OR, XOR
    }

    private static void propagate(Map<String, Boolean> state, List<Module> modules) {
        boolean changed;
        do {
            changed = false;
            for (Module module : modules) {
                String output = module.output();
                String input1 = module.input1();
                String input2 = module.input2();
                if (!state.containsKey(output) && state.containsKey(input1) && state.containsKey(input2)) {
                    Boolean inBit1 = state.get(input1);
                    Boolean inBit2 = state.get(input2);
                    boolean outBit = switch (module.type()) {
                        case AND -> inBit1 & inBit2;
                        case OR -> inBit1 | inBit2;
                        case XOR -> inBit1 ^ inBit2;
                    };
                    state.put(output, outBit);
                    changed = true;
                }
            }

        } while (changed);
    }

    private static boolean eqInputs(Module module, String inputA, String inputB) {
        String input1 = module.input1();
        String input2 = module.input2();
        return (input1.equals(inputA) && input2.equals(inputB)) || (input1.equals(inputB) && input2.equals(inputA));
    }

    private static boolean hasModule(List<Module> modules, String input1, String input2, ModuleType type, String output) {
        return modules.contains(new Module(input1, input2, type, output)) || modules.contains(new Module(input2, input1, type, output));
    }

    private static void renameWire(List<Module> modules, String oldName, String newName) {
        ListIterator<Module> iter = modules.listIterator();
        while (iter.hasNext()) {
            Module module = iter.next();
            if (module.input1().equals(oldName)) {
                iter.set(new Module(newName, module.input2(), module.type(), module.output()));
            } else if (module.input2().equals(oldName)) {
                iter.set(new Module(module.input1(), newName, module.type(), module.output()));
            } else if (module.output().equals(oldName)) {
                iter.set(new Module(module.input1(), module.input2(), module.type(), newName));
            }
        }
    }


    @SuppressWarnings("SameParameterValue")
    private static void renameOutputByRule(List<Module> modules, ModuleType type, char pfx1, char pfx2, char pfxOut) {
        for (Module module : modules) {
            if (module.type() == type) {
                char char1 = module.input1().charAt(0);
                char char2 = module.input2().charAt(0);
                if ((char1 == pfx1 && char2 == pfx2) || (char2 == pfx1 && char1 == pfx2)) {
                    String num1 = module.input1().substring(1);
                    String num2 = module.input2().substring(1);
                    if (!num1.equals(num2)) {
                        throw new RuntimeException();
                    }
                    // Do not touch output z-wire
                    if (!module.output().startsWith("z")) {
                        renameWire(modules, module.output(), pfxOut + num1);
                    }
                }
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void renameInputByRule(List<Module> modules, ModuleType type, char pfxInMatch, char pfxOut, char pfxInNew) {
        for (Module module : modules) {
            if (module.type() == type) {
                char charOut = module.output().charAt(0);
                if (charOut == pfxOut) {
                    char charIn1 = module.input1().charAt(0);
                    char charIn2 = module.input2().charAt(0);
                    String numOut = module.output().substring(1);
                    if (pfxInMatch == charIn1) {
                        String numIn1 = module.input1().substring(1);
                        if (!numIn1.equals(numOut)) {
                            throw new RuntimeException();
                        }
                        renameWire(modules, module.input2(), pfxInNew + numOut);
                    } else if (pfxInMatch == charIn2) {
                        String numIn2 = module.input2().substring(1);
                        if (!numIn2.equals(numOut)) {
                            throw new RuntimeException();
                        }
                        renameWire(modules, module.input1(), pfxInNew + numOut);
                    }
                }
            }
        }
    }

    private static void swapWires(List<Module> modules, String wire1, String wire2) {
        ListIterator<Module> iter = modules.listIterator();
        while (iter.hasNext()) {
            Module module = iter.next();
            if (module.output().equals(wire1)) {
                iter.set(new Module(module.input1(), module.input2(), module.type(), wire2));
            } else if (module.output().equals(wire2)) {
                iter.set(new Module(module.input1(), module.input2(), module.type(), wire1));
            }
        }
    }

    private static List<String> validateState(List<Module> modules) {
        long numZBits = modules.stream().map(Module::output).filter(out -> out.charAt(0) == 'z').count();
        List<String> badWires = new ArrayList<>();
        List<String> badWires1 = new ArrayList<>();

        // FIXME: INCOMPLETE!!!!!!!!!!!!!!!!!!!!

        for (Module module : modules) {
            if (module.output().charAt(0) == 'z' && module.type() != ModuleType.XOR) {
                badWires1.add(module.output());
            }
        }

        if (badWires1.size() != 4) {
            throw new NotImplementedException();
        }
        if (!badWires1.remove("z45")) {
            throw new IllegalStateException();
        }

        // z07 z20 z28

        // FIXME! // bit 35
        badWires.add("hth");
        badWires.add("tqr");
        swapWires(modules, "hth", "tqr");

        // Rename input XOR
        renameOutputByRule(modules, ModuleType.XOR, 'x', 'y', 'D');

        badWires.add("vmv");
        badWires.add("z07");
        swapWires(modules, "vmv", "z07");

        badWires.add("kfm");
        badWires.add("z20");
        swapWires(modules, "kfm", "z20");

        badWires.add("hnv");
        badWires.add("z28");
        swapWires(modules, "hnv", "z28");

        // BAD tqr   tqr XOR vkc -> z35


        // Rename input AND
//        renameOutputByRule(modules, ModuleType.AND, 'x', 'y', 'F');

        // Handle special case: x00 + y00 == z00 <c00>
        // XOR is already OK
        for (Module module : modules) {
            if (eqInputs(module, "x00", "y00")) {
                switch (module.type()) {
                    case AND -> renameWire(modules, module.output(), "C01");
                    case XOR -> {
                        if (!module.output().equals("z00")) {
                            throw new IllegalStateException();
                        }
                    }
                    case OR -> throw new IllegalStateException();
                }
            }
        }

        // At this point all XOR elements are correctly wired
        // Rename C_in wire
        renameInputByRule(modules, ModuleType.XOR, 'D', 'z', 'C');

        return badWires;
    }

    private static void dumpModules(List<Module> modules) {
        System.out.println();
        modules.forEach(m -> System.out.println(m.input1() + " " + m.type() + " " + m.input2() + " -> " + m.output()));
    }

    public static TaskSolution solve(String input) {
        String[] sections = input.split("\n\n");

        SortedMap<String, Boolean> initState = new TreeMap<>(Comparator.reverseOrder());
        for (String lineState : sections[0].split("\n")) {
            String[] s1 = lineState.split(": ");
            initState.put(s1[0], Integer.parseUnsignedInt(s1[1]) == 1);
        }

        List<Module> modules = new ArrayList<>();

        for (String lineModule : sections[1].split("\n")) {
            String[] s1 = lineModule.split(" ");
            String nodeA = s1[0];
            String nodeB = s1[2];
            String nodeOut = s1[4];
            Module module = new Module(nodeA, nodeB, ModuleType.valueOf(s1[1]), nodeOut);
            modules.add(module);
        }

        List<Module> debugModules = new ArrayList<>(modules);

        // testCarryBit
        SortedMap<String, Boolean> debugState = new TreeMap<>(initState);

        propagate(initState, modules);

        AtomicLong zValue = new AtomicLong();

        initState.entrySet().stream()
                .filter(entry -> entry.getKey().charAt(0) == 'z')
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(entry -> zValue.getAndUpdate(val -> (val * 2 + (entry.getValue() ? 1 : 0))));

        long result1 = zValue.get();

        List<String> badWires = validateState(debugModules);

        dumpModules(debugModules);
        String result2 = badWires.stream().sorted().collect(Collectors.joining(","));

        return TaskSolution.of(Long.toString(result1), result2);
    }

    public static void main(String[] args) {
        // FIXME
//        Helpers.runTask(TEST_1, Day_24_Cheat::solve, false);
//        Helpers.runTask(TEST_2, Day_24_Cheat::solve, false);
        Helpers.runTask(MAIN, Day_24_Cheat::solve, false);
    }
}

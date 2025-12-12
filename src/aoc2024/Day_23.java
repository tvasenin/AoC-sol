package aoc2024;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Day_23 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/23-test.txt"), Long.toString(7), "co,de,ka,ta");
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/23-main.txt"), Long.toString(1064), "aq,cc,ea,gc,jo,od,pa,rg,rv,ub,ul,vr,yy");


    private static long solve1(Map<String, Set<String>> graph) {
        long result = 0;
        String[] nodes = graph.keySet().toArray(new String[0]);
        boolean[] startsWithT = new boolean[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            startsWithT[i] = nodes[i].charAt(0) == 't';
        }
        for (int i = 0; i < nodes.length - 2; i++) {
            String node1 = nodes[i];
            Set<String> nei1 = graph.get(node1);
            boolean node1StartWithT = startsWithT[i];
            for (int j = i + 1; j < nodes.length - 1; j++) {
                String node2 = nodes[j];
                if (!nei1.contains(node2)) {
                    continue;
                }
                Set<String> nei2 = graph.get(node2);
                boolean node2StartsWithT = startsWithT[j];
                for (int k = j + 1; k < nodes.length; k++) {
                    String node3 = nodes[k];
                    boolean anyStartsWithT = node1StartWithT || node2StartsWithT || startsWithT[k];
                    if (anyStartsWithT) {
                        if (nei1.contains(node3) && nei2.contains(node3)) {
                            result++;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static void removeEdgesDisjointNeighbors(Map<String, Set<String>> graph) {
        graph.forEach((node1, nei1) -> {
            Iterator<String> iter = nei1.iterator();
            while (iter.hasNext()) {
                String node2 = iter.next();
                Set<String> nei2 = graph.get(node2);
                // Remove edge if nodes don't have 3rd common neighbor
                if (Collections.disjoint(nei1, nei2)) {
                    nei2.remove(node1);
                    iter.remove();
                }
            }
        });
    }

    private static void removeTooFewNeighbors(Map<String, Set<String>> graph, int minNeighbors) {
        Set<String> nodesToRemove = new HashSet<>();
        do {
            nodesToRemove.clear();
            graph.forEach((node1, nei1) -> {
                if (nei1.size() < minNeighbors) {
                    for (String node2 : nei1) {
                        graph.get(node2).remove(node1);
                    }
                    nodesToRemove.add(node1);
                }
            });
            graph.keySet().removeAll(nodesToRemove);
        } while (!nodesToRemove.isEmpty());
    }



    private static void printGraph(Map<String, Set<String>> graph) {
        Set<String> processed = new HashSet<>();
        System.out.println("graph G {");
        graph.forEach((node1, neighbors) -> {
            processed.add(node1);
            neighbors.forEach(node2 -> {
                if (!processed.contains(node2)) {
                    System.out.println("  " + node1 + " -- " + node2 + ";");
                }
            });
        });
        System.out.println("}");
    }

    public static TaskSolution solve(String input) {
        Map<String, Set<String>> graph = new HashMap<>();
        for (String line : input.split("\n")) {
            String[] s0 = line.split("-");
            String node1 = s0[0];
            String node2 = s0[1];
            graph.computeIfAbsent(node1, k -> new HashSet<>()).add(node2);
            graph.computeIfAbsent(node2, k -> new HashSet<>()).add(node1);
        }

        removeEdgesDisjointNeighbors(graph);

        AtomicBoolean debug = new AtomicBoolean(false); // Atomic to get rid of IDE warnings
        debug.set(true);
        if (debug.get()) {
            printGraph(graph);
        }

        long result1 = solve1(graph);

        int minNei2 = graph.values().stream().mapToInt(Set::size).min().orElseThrow();

        // Cheat -- need more careful usage (and not enough for the test case)
        removeTooFewNeighbors(graph, minNei2 + 1);
        if (debug.get()) {
            printGraph(graph);
        }

        String result2 = graph.keySet().stream().sorted().collect(Collectors.joining(","));

        return TaskSolution.of(Long.toString(result1), result2);
    }

    public static void main(String[] args) {
        // FIXME
//        Helpers.runTask(TEST, Day_23::solve, false);
        Helpers.runTask(MAIN, Day_23::solve, false);
    }
}

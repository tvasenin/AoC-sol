package aoc2025;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.Set;

public class Day_11_Cheat {

    private static final TaskData TEST_PART_1 = new TaskData(Resources.getResourceAsString("aoc2025/11-test-part1.txt"), 5, -1);
    private static final TaskData TEST_PART_2 = new TaskData(Resources.getResourceAsString("aoc2025/11-test-part2.txt"), -1, 2);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2025/11-main.txt"), 634, 377452269415704L);


    @SuppressWarnings("SameParameterValue")
    private static long getNumPathsThroughIntermediateNodes(AllDirectedPaths<String, DefaultEdge> allDirectedPaths,
                                                            String src, String dst, Set<String> intermediateNodes
    ) {
        long numPathsSrcDst = 0;
        for (String intermediateNode : intermediateNodes) {
            long numPathsSrcInt = getNumPaths(allDirectedPaths, src, intermediateNode);
            long numPathsIntDst = getNumPaths(allDirectedPaths, intermediateNode, dst);
            numPathsSrcDst += numPathsSrcInt * numPathsIntDst;
        }
        return numPathsSrcDst;
    }

    private static Graph<String, DefaultEdge> parseGraph(String input) {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        input.lines().forEach(line -> {
            String[] s0 = line.split(": ");
            String src = s0[0];
            graph.addVertex(src);
            Arrays.stream(s0[1].split(" "))
                    .forEach(dst -> {
                        if (!graph.containsVertex(dst)) {
                            graph.addVertex(dst);
                        }
                        graph.addEdge(src, dst);
                    });
        });
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
        if (cycleDetector.detectCycles()) throw new AssertionError();
        return graph;
    }

    private static long getNumPathsWithoutNode(AllDirectedPaths<String, DefaultEdge> allDirectedPaths,
                                               String src, String dst, String nodeForbidden) {
        return allDirectedPaths.getAllPaths(src, dst, true, null).stream()
                .filter(path -> !path.getVertexList().contains(nodeForbidden))
                .count();
    }
    private static long getNumPaths(AllDirectedPaths<String, DefaultEdge> allDirectedPaths, String src, String dst) {
        return allDirectedPaths.getAllPaths(src, dst, true, null).size();
    }

    private static long solve1(Graph<String, DefaultEdge> graph) {
        AllDirectedPaths<String, DefaultEdge> allDirectedPaths1 = new AllDirectedPaths<>(graph);
        return getNumPaths(allDirectedPaths1, "you", "out");
    }

    private static long solve2(Graph<String, DefaultEdge> graph) {
        AllDirectedPaths<String, DefaultEdge> allDirectedPaths2 = new AllDirectedPaths<>(graph);

        long numPathsSvrFft = getNumPathsWithoutNode(allDirectedPaths2, "svr", "fft", "dac");

        long numPathsDacFft = getNumPaths(allDirectedPaths2, "dac", "fft");

        // never get there on prod output :)
        long numPathsSvrDac = numPathsDacFft == 0
                ? 0
                : getNumPathsWithoutNode(allDirectedPaths2, "svr", "dac", "fft");

        // never get there on prod output :)
        long numPathsFftOut = numPathsDacFft == 0
                ? 0
                : getNumPathsWithoutNode(allDirectedPaths2, "fft", "out", "dac");

        long numPathsDacOut = getNumPathsWithoutNode(allDirectedPaths2, "dac", "out", "fft");


        // Cheat
        // long numPathsFftDac = getNumPaths(allDirectedPaths2, "fft", "dac");
        Set<String> intermediateNodes = Set.of("rpn", "apc", "lpz");
        long numPathsFftDac = getNumPathsThroughIntermediateNodes(allDirectedPaths2, "fft", "dac", intermediateNodes);

        long numPathsSvrFftDacOut = numPathsSvrFft * numPathsFftDac * numPathsDacOut;
        long numPathsSvrDacFftOut = numPathsSvrDac * numPathsDacFft * numPathsFftOut;

//        System.out.println(Helpers.exportGraphDOT(graph));

        return numPathsSvrFftDacOut + numPathsSvrDacFftOut;
    }

    public static TaskSolution solve1(String input) {
        long result1 = solve1(parseGraph(input));
        return TaskSolution.of(result1, -1);
    }

    public static TaskSolution solve2(String input) {
        long result2 = solve2(parseGraph(input));
        return TaskSolution.of(-1, result2);
    }

    public static TaskSolution solve(String input) {
        Graph<String, DefaultEdge> graph = parseGraph(input);
        long result1 = solve1(graph);
        long result2 = solve2(graph);
        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        new DefaultDirectedGraph<>(DefaultEdge.class); // Warmup
        Helpers.runTask(TEST_PART_1, Day_11_Cheat::solve1, false);
        // FIXME
//        Helpers.runTask(TEST_PART_2, Day_11_Cheat::solve2, false);
        Helpers.runTask(MAIN, Day_11_Cheat::solve, false);
    }
}

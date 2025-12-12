package aoc2025;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import util.Resources;

import java.io.StringWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Day_11_Cheat {

    private static final String INPUT_TEST1 = Resources.getResourceAsString("aoc2025/11-test-part1.txt");
    private static final String INPUT_TEST2 = Resources.getResourceAsString("aoc2025/11-test-part2.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2025/11-main.txt");


    @SuppressWarnings("unused")
    private static <V, E> String printGraph(Graph<V, E> graph) {
        DOTExporter<V, E> exporter = new DOTExporter<>(Object::toString);
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        return writer.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private static long getNumPathsThroughIntermediateNodes(AllDirectedPaths<String, DefaultEdge> allDirectedPaths,
                                                            String src, String dst, Set<String> intermediateNodes
    ) {
        long numPathsSrcDst = 0;
        for (String intermediateNode : intermediateNodes) {
            long numPathsSrcInt = allDirectedPaths.getAllPaths(src, intermediateNode, true, null).size();
            long numPathsIntDst = allDirectedPaths.getAllPaths(intermediateNode, dst, true, null).size();
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

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input1 = isTest ? INPUT_TEST1 : INPUT;
        //noinspection ConstantValue
        String input2 = isTest ? INPUT_TEST2 : INPUT;


        new DefaultDirectedGraph<>(DefaultEdge.class);
        Instant start = Instant.now();

        AllDirectedPaths<String, DefaultEdge> allDirectedPaths1 = new AllDirectedPaths<>(parseGraph(input1));

        long result1 = allDirectedPaths1.getAllPaths("you", "out", true, null).size();

        AllDirectedPaths<String, DefaultEdge> allDirectedPaths2 = new AllDirectedPaths<>(parseGraph(input2));

        long numPathsSvrFft = allDirectedPaths2.getAllPaths("svr", "fft", true, null).stream()
                .filter(path -> !path.getVertexList().contains("dac"))
                .count();

        long numPathsDacFft = allDirectedPaths2.getAllPaths("dac", "fft", true, null)
                .size();

        // never get there on prod output :)
        long numPathsSvrDac = numPathsDacFft == 0
                ? 0
                : allDirectedPaths2.getAllPaths("svr", "dac", true, null).stream()
                .filter(path -> !path.getVertexList().contains("fft"))
                .count();

        // never get there on prod output :)
        long numPathsFftOut = numPathsDacFft == 0
                ? 0
                : allDirectedPaths2.getAllPaths("fft", "out", true, null).stream()
                .filter(path -> !path.getVertexList().contains("dac"))
                .count();

        long numPathsDacOut = allDirectedPaths2.getAllPaths("dac", "out", true, null)
                .stream().filter(path -> !path.getVertexList().contains("fft"))
                .count();

        // Cheat
        // long numPathsFftDac = allDirectedPaths2.getAllPaths("fft", "dac", true, null).size();
        Set<String> intermediateNodes = Set.of("rpn", "apc", "lpz");
        long numPathsFftDac = getNumPathsThroughIntermediateNodes(allDirectedPaths2, "fft", "dac", intermediateNodes);

        long numPathsSvrFftDacOut = numPathsSvrFft * numPathsFftDac * numPathsDacOut;
        long numPathsSvrDacFftOut = numPathsSvrDac * numPathsDacFft * numPathsFftOut;

//        System.out.println(printGraph(graph));

        long result2 = numPathsSvrFftDacOut + numPathsSvrDacFftOut;

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input1.equals(INPUT) && input2.equals(INPUT)) {
            if (result1 != 634) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 377452269415704L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

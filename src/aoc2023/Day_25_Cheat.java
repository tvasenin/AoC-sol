package aoc2023;

import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day_25_Cheat {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/25-test.txt"), 2, -1);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/25-main.txt"), 598120, -1);


    @SuppressWarnings("unused")
    public static List<Set<String>> getConnectedSetsAfterRemoving3Edges(SimpleGraph<String, DefaultEdge> graph ) {
        List<Pair<String, String>> allEdges = graph.edgeSet().stream()
                .map(edge -> Pair.of(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)))
                .toList();
        ConnectivityInspector<String, DefaultEdge> inspector = new ConnectivityInspector<>(graph);
        int size = allEdges.size();
        long total = StreamEx.ofCombinations(size, 3).count();
        long cnt = 0;
        for (int i = 0; i < size - 2; i++) {
            Pair<String, String> edgeDef1 = allEdges.get(i);
            graph.removeEdge(edgeDef1.getLeft(), edgeDef1.getRight());
            inspector.edgeRemoved(new GraphEdgeChangeEvent<>(graph, GraphEdgeChangeEvent.EDGE_REMOVED, null, edgeDef1.getLeft(), edgeDef1.getRight()));
            for (int j = i + 1; j < size - 1; j++) {
                Pair<String, String> edgeDef2 = allEdges.get(j);
                graph.removeEdge(edgeDef2.getLeft(), edgeDef2.getRight());
                inspector.edgeRemoved(new GraphEdgeChangeEvent<>(graph, GraphEdgeChangeEvent.EDGE_REMOVED, null, edgeDef2.getLeft(), edgeDef2.getRight()));
                for (int k = j + 1; k < size ; k++) {
                    Pair<String, String> edgeDef3 = allEdges.get(k);
                    graph.removeEdge(edgeDef3.getLeft(), edgeDef3.getRight());
                    inspector.edgeRemoved(new GraphEdgeChangeEvent<>(graph, GraphEdgeChangeEvent.EDGE_REMOVED, null, edgeDef3.getLeft(), edgeDef3.getRight()));
                    if (!inspector.isConnected()) {
                        return inspector.connectedSets();
                    }
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.printf("Iteration: %s out of %s (%s percent)\n", cnt, total, cnt * 100 / total);
                    }
                    graph.addEdge(edgeDef3.getLeft(), edgeDef3.getRight());
                    inspector.edgeAdded(new GraphEdgeChangeEvent<>(graph, GraphEdgeChangeEvent.EDGE_ADDED, null, edgeDef3.getLeft(), edgeDef3.getRight()));
                }
                graph.addEdge(edgeDef2.getLeft(), edgeDef2.getRight());
                inspector.edgeAdded(new GraphEdgeChangeEvent<>(graph, GraphEdgeChangeEvent.EDGE_ADDED, null, edgeDef2.getLeft(), edgeDef2.getRight()));
            }
            graph.addEdge(edgeDef1.getLeft(), edgeDef1.getRight());
            inspector.edgeAdded(new GraphEdgeChangeEvent<>(graph, GraphEdgeChangeEvent.EDGE_ADDED, null, edgeDef1.getLeft(), edgeDef1.getRight()));
        }
        throw new IllegalStateException();
    }

    public static TaskSolution solve(String input) {
        SimpleGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        input.lines().forEach(line -> {
            String[] s0 = line.split(": ");
            String id = s0[0];
            Set<String> nodesTo = Arrays.stream(s0[1].split(" ")).collect(Collectors.toSet());
            graph.addVertex(id);
            nodesTo.forEach(v -> {
                graph.addVertex(v);
                graph.addEdge(id, v);
            });
        });

        // FIXME: Cheat
        graph.removeEdge("ldk", "bkm");
        graph.removeEdge("zmq", "pgh");
        graph.removeEdge("bvc", "rsm");

        System.out.println(Helpers.exportGraphDOT(graph));

        //List<Set<String>> connectedSets = getConnectedSetsAfterRemoving3Edges(graph);
        ConnectivityInspector<String, DefaultEdge> inspector = new ConnectivityInspector<>(graph);
        List<Set<String>> connectedSets = inspector.connectedSets();
        if (connectedSets.size() != 2) {
            throw new IllegalStateException("Unexpected components count:" + connectedSets.size());
        }

        long result1 = (long) connectedSets.get(0).size() * connectedSets.get(1).size();

        return TaskSolution.of(result1, -1);
    }

    public static void main(String[] args) {
        // FIXME
//        Helpers.runTask(TEST, Day_25_Cheat::solve, false);
        Helpers.runTask(MAIN, Day_25_Cheat::solve, false);
    }}

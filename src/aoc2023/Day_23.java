package aoc2023;

import org.apache.commons.lang3.tuple.Pair;
import util.Cell;
import util.CharField;
import util.Direction;
import util.Move;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day_23 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2023/23-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2023/23-main.txt");


    @SuppressWarnings("SpellCheckingInspection")
    private static final Set<Direction> ULDR = Set.of(Direction.U, Direction.L, Direction.D, Direction.R);

    private record Edge(Cell nodeFrom, Cell nodeTo, int weight) { }

    private record EdgeIntLight(int nodeTo, int weight) { }

    private static boolean isNode(CharField field, Cell cell) {
        if (field.get(cell) != '.') {
            return false;
        }
        int row = cell.row();
        int col = cell.col();
        boolean isStartNode = row == 0 && col == 1;
        boolean isEndNode = row == field.numRows - 1 && col == field.numCols - 2;
        if (isStartNode || isEndNode) {
            return true;
        }
        List<Character> chars = field.getNeighborValuesStream(row, col, true).toList();
        return !chars.contains('.')
                && (chars.contains('<') || chars.contains('>') || chars.contains('^') || chars.contains('v'));
    }

    private static Cell getCellNoBoundaryCheck(Cell cell, Direction dir) {
        int row = cell.row();
        int col = cell.col();
        return switch (dir) {
            case U -> new Cell(row - 1, col);
            case D -> new Cell(row + 1, col);
            case L -> new Cell(row, col - 1);
            case R -> new Cell(row, col + 1);
        };
    }

    private static boolean isPassableGate(char c, Direction dir) {
        return switch (dir) {
            case U -> c == '^';
            case L -> c == '<';
            case D -> c == 'v';
            case R -> c == '>';
        };
    }

    @SuppressWarnings("unused")
    private static Edge getEdgeNoBoundaryCheck(CharField field, Cell nodeFrom, Direction startDir) {
        Move move = new Move(getCellNoBoundaryCheck(nodeFrom, startDir), startDir);
        int len = 1;

        while (!isNode(field, move.cell())) {
            Move finalMove = move;
            move = ULDR.stream()
                    .filter(newDir -> newDir != finalMove.dir().reverse())
                    .map(newDir -> new Move(getCellNoBoundaryCheck(finalMove.cell(), newDir), newDir))
                    .filter(nextMove -> {
                        char c = field.get(nextMove.cell());
                        return c == '.' || isPassableGate(c, nextMove.dir());
                    })
                    .findFirst().orElseThrow();
            len++;
        }
        return new Edge(nodeFrom, move.cell(), len);
    }

    private static Edge getEdgeNoBoundaryCheckOptimized(CharField field, Cell nodeFrom, Direction startDir) {
        Move move = new Move(getCellNoBoundaryCheck(nodeFrom, startDir), startDir);
        int len = 1;
        AtomicBoolean wentPastGate = new AtomicBoolean(false);
        AtomicBoolean isNode = new AtomicBoolean(false);

        while (!(isNode.get() || move.cell().row() == field.numRows - 1)) {
            Move finalMove = move;
            move = ULDR.stream()
                    .filter(newDir -> newDir != finalMove.dir().reverse())
                    .map(newDir -> new Move(getCellNoBoundaryCheck(finalMove.cell(), newDir), newDir))
                    .filter(nextMove -> {
                        char c = field.get(nextMove.cell());
                        return switch (c) {
                            case '.' -> { isNode.set(wentPastGate.get()); yield true; }
                            case '#' -> false;
                            case '>' -> { wentPastGate.set(true); yield nextMove.dir() == Direction.R; }
                            case 'v' -> { wentPastGate.set(true); yield nextMove.dir() == Direction.D; }
                            case '<' -> { wentPastGate.set(true); yield nextMove.dir() == Direction.L; }
                            case '^' -> { wentPastGate.set(true); yield nextMove.dir() == Direction.U; }
                            default -> throw new IllegalStateException("Unexpected value: " + c);
                        };
                    })
                    .findFirst().orElseThrow();
            len++;
        }
        return new Edge(nodeFrom, move.cell(), len);
    }

    private static Map<Cell, Set<Edge>> buildDag(CharField field, Cell startCell, Cell endCell, Direction startDir) {
        Map<Cell, Set<Edge>> dag = new HashMap<>();
        Move moveFromStart = new Move(startCell, startDir);
        Queue<Move> queue = new ArrayDeque<>();
        queue.add(moveFromStart);
        dag.put(endCell, Collections.emptySet());
        while (!queue.isEmpty()) {
            Move moveFrom = queue.remove();
            Cell nodeFrom = moveFrom.cell();
//            Edge edge = getEdgeNoBoundaryCheck(field, nodeFrom, moveFrom.dir());
            Edge edge = getEdgeNoBoundaryCheckOptimized(field, nodeFrom, moveFrom.dir());
            if (!edge.nodeFrom().equals(nodeFrom)) {
                throw new IllegalStateException();
            }
            dag.computeIfAbsent(edge.nodeFrom(), k -> new HashSet<>()).add(edge);
            Cell nodeTo = edge.nodeTo();
            if (!dag.containsKey(nodeTo)) {
                // choose next directions
                Set<Direction> nextDirections = ULDR.stream()
                        .map(dir -> new Move(getCellNoBoundaryCheck(nodeTo, dir), dir))
                        .map(move -> Pair.of(field.get(move.cell()), move.dir()))
                        .filter(p -> isPassableGate(p.getLeft(), p.getRight())).map(Pair::getRight)
                        .collect(Collectors.toSet());
                for (Direction nextDirection : nextDirections) {
                    queue.add(new Move(nodeTo, nextDirection));
                }
            }
        }
        return dag;
    }

    @SuppressWarnings("SameParameterValue")
    private static int getLongestPathDAGInt(List<EdgeIntLight>[] dag, int startNode, int endNode) {
        // Since graph G is DAG, longest path is the negated shortest path in -G
        Instant instantBegin = Instant.now();
        int count = 0;
        Queue<Pair<Integer, Integer>> queue = new ArrayDeque<>();
        int[] bestWeights = new int[dag.length]; // No need to fill with MaxInt since all weights are negative
        int minWeight = Integer.MAX_VALUE;
        queue.add(Pair.of(startNode, 0));
        while (!queue.isEmpty()) {
            count++;
            Pair<Integer, Integer> pair = queue.remove();
            int nodeFrom = pair.getLeft();
            int curWeight = pair.getRight();
            for (EdgeIntLight edge : dag[nodeFrom]) {
                int nodeTo = edge.nodeTo();
                int newWeight = curWeight + (-edge.weight()); // negated weight
                int bestWeight = bestWeights[nodeTo];
                if (newWeight < bestWeight) {
                    bestWeights[nodeTo] = newWeight;
                    if (nodeTo == endNode) {
                        minWeight = Math.min(minWeight, newWeight);
                    } else {
                        queue.add(Pair.of(nodeTo, newWeight));
                    }
                }
            }
        }
        Instant instantEnd = Instant.now();
        Duration duration = Duration.between(instantBegin, instantEnd);
        System.out.printf("%d states processed in %f seconds\n", count, duration.toMillis() / 1000f);
        return -minWeight; // negated weight
    }

    private static void updateLongestSimplePathInt(List<EdgeIntLight>[] graph, int curNode,
                                                   boolean[] visitedNodes, int curWeight, AtomicInteger bestWeight) {
        // Simple == no duplicate edges
        // Bruteforce all paths
        for (EdgeIntLight edge : graph[curNode]) {
            int nodeTo = edge.nodeTo();
            if (!visitedNodes[nodeTo]) {
                int newWeight = curWeight + edge.weight();
                if (nodeTo == graph.length - 1) {
                    // End node
                    if (newWeight > bestWeight.get()) {
                        bestWeight.set(newWeight);
                    }
                } else {
                    visitedNodes[curNode] = true;
                    updateLongestSimplePathInt(graph, nodeTo, visitedNodes, newWeight, bestWeight);
                    visitedNodes[curNode] = false;
                }
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static long getLongestSimplePathInt(List<EdgeIntLight>[] graph) {
        AtomicInteger longestPath = new AtomicInteger(0);
        updateLongestSimplePathInt(graph, 0, new boolean[graph.length], 0, longestPath);
        return longestPath.get();
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        CharField field = CharField.of(input);

        Instant start = Instant.now();

        Cell startCell = new Cell(0, 1);
        Cell endCell = new Cell(field.numRows - 1, field.numCols - 2);
        Direction startDir = Direction.D;
        Map<Cell, Set<Edge>> dag = buildDag(field, startCell, endCell, startDir);

        Map<Cell, Integer> cell2Id = new HashMap<>(dag.size());
        cell2Id.put(startCell, 0);
        cell2Id.put(endCell, dag.size() - 1);
        int id = 1;
        for (Cell cell : dag.keySet()) {
            if (cell.equals(startCell) || cell.equals(endCell)) {
                continue;
            }
            cell2Id.put(cell, id++);
        }

        // Convert to optimized DAG
        @SuppressWarnings("unchecked")
        List<EdgeIntLight>[] graphInt = new List[dag.size()];
        dag.forEach((key, value) -> {
            int cellId = cell2Id.get(key);
            List<EdgeIntLight> edges = new ArrayList<>(value.size());
            for (Edge edge : value) {
                EdgeIntLight edgeInt = new EdgeIntLight(cell2Id.get(edge.nodeTo()), edge.weight());
                edges.add(edgeInt);
            }
            graphInt[cellId] = edges;
        });


        long result1 = getLongestPathDAGInt(graphInt, 0, graphInt.length - 1);

        // Add reverse edges
        dag.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .forEach(edge -> {
                    // add edge with swapped nodes
                    EdgeIntLight swapped = new EdgeIntLight(cell2Id.get(edge.nodeFrom()), edge.weight());
                    List<EdgeIntLight> edges = graphInt[cell2Id.get(edge.nodeTo())];
                    if (!edges.contains(swapped)) {
                        edges.add(swapped);
                    }
                });

        long result2 = getLongestSimplePathInt(graphInt);

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 2010) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 6318) {
                System.out.println("Wrong Result 2");
            }
        }

        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

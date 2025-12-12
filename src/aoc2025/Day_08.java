package aoc2025;

import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Day_08 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2025/08-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2025/08-main.txt");


    private record Point3D(long x, long y, long z) {
        public static long sqrDistance(Point3D p1, Point3D p2) {
            long dx = p1.x - p2.x;
            long dy = p1.y - p2.y;
            long dz = p1.z - p2.z;
            return dx * dx + dy * dy + dz * dz;
        }
    }

    private static Pair<Point3D, Point3D> getLastConnectedPairForPart2(List<Point3D> points) {
        long maxDist = Long.MIN_VALUE;
        int idx1 = -1;
        int idx2 = -1;
        for (int i = 0; i < points.size(); i++) {
            long minDist = Long.MAX_VALUE;
            int minIdx = -1;
            Point3D point1 = points.get(i);
            for (int j = 0; j < points.size(); j++) {
                if (i == j) {
                    continue;
                }
                long sqrDist = Point3D.sqrDistance(point1, points.get(j));
                if (sqrDist < minDist) {
                    minDist = sqrDist;
                    minIdx = j;
                }
            }
            if (minDist > maxDist) {
                maxDist = minDist;
                idx1 = i;
                idx2 = minIdx;
            }
        }
        return Pair.of(points.get(idx1), points.get(idx2));
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;
        //noinspection ConstantValue
        int numEdges1 = isTest ? 10 : 1000;

        Instant start = Instant.now();

        List<Point3D> points = input.lines()
                .map(line -> {
                    String[] s = line.split(",");
                    return new Point3D(Long.parseLong(s[0]), Long.parseLong(s[1]), Long.parseLong(s[2]));
                })
                .toList();

        List<Pair<Point3D, Point3D>> edges1 = StreamEx.ofPairs(points, Pair::of)
                .sorted(Comparator.comparingLong(pair -> Point3D.sqrDistance(pair.getLeft(), pair.getRight())))
                .limit(numEdges1)
                .toList();

        SimpleGraph<Point3D, DefaultEdge> graph1 = new SimpleGraph<>(DefaultEdge.class);

        points.forEach(graph1::addVertex);
        edges1.forEach(e -> graph1.addEdge(e.getLeft(), e.getRight()));

        ConnectivityInspector<Point3D, DefaultEdge> inspector1 = new ConnectivityInspector<>(graph1);
        List<Set<Point3D>> connectedSets1 = inspector1.connectedSets();

        long result1 = connectedSets1.stream()
                .map(Set::size)
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToLong(i-> i)
                .reduce(1, (a , b) -> a * b);

        Pair<Point3D, Point3D> lastConnectedPair = getLastConnectedPairForPart2(points);

        long result2 = lastConnectedPair.getLeft().x * lastConnectedPair.getRight().x;

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 122636L) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 9271575747L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

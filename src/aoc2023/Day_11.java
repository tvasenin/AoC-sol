package aoc2023;

import one.util.streamex.EntryStream;
import util.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Day_11 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2023/11-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2023/11-main.txt");


    record Planet(int row, int col) { }

    static int getOriginalDistance(Planet planetA, Planet planetB) {
        int distanceOrigI = Math.abs(planetA.row - planetB.row);
        int distanceOrigJ = Math.abs(planetA.col - planetB.col);
        return distanceOrigI + distanceOrigJ;
    }

    static long getDistance(Planet planetA, Planet planetB, int[] emptyBeforeI, int[] emptyBeforeJ, int numReplaces) {
        long sparseI = Math.abs(emptyBeforeI[planetA.row] - emptyBeforeI[planetB.row]);
        long sparseJ = Math.abs(emptyBeforeJ[planetA.col] - emptyBeforeJ[planetB.col]);
        return getOriginalDistance(planetA, planetB) + (sparseI + sparseJ) * (numReplaces - 1);
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        String[] lines = input.lines().toArray(String[]::new);

        int size = lines.length;

        List<Planet> planets = new ArrayList<>();
        boolean[] emptyI = new boolean[size];
        boolean[] emptyJ = new boolean[size];
        Arrays.fill(emptyI, true);
        Arrays.fill(emptyJ, true);
        for (int row = 0; row < lines.length; row++) {
            String line = lines[row];
            for (int col = 0; col < line.length(); col++) {
                if (line.charAt(col) == '#') {
                    planets.add(new Planet(row, col));
                    emptyI[row] = false;
                    emptyJ[col] = false;
                }
            }
        }

        int[] emptyBeforeI = new int[size];
        int[] emptyBeforeJ = new int[size];
        int ci = 0, cj = 0;
        for (int i = 0; i < size; i++) {
            emptyBeforeI[i] = ci;
            emptyBeforeJ[i] = cj;
            if (emptyI[i]) {
                ci++;
            }
            if (emptyJ[i]) {
                cj++;
            }
        }

        AtomicLong sum1 = new AtomicLong();
        AtomicLong sum2 = new AtomicLong();
        EntryStream.ofPairs(planets).forKeyValue((planetA, planetB) -> {
            sum1.addAndGet(getDistance(planetA, planetB, emptyBeforeI, emptyBeforeJ, 2));
            sum2.addAndGet(getDistance(planetA, planetB, emptyBeforeI, emptyBeforeJ, 1000000));
        });
        long result1 = sum1.get();
        long result2 = sum2.get();

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 9556896) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 685038186836L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

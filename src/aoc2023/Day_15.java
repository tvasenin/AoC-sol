package aoc2023;

import org.apache.commons.lang3.StringUtils;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Day_15 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2023/15-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2023/15-main.txt");


    static int getHash(String s) {
        return s.chars().reduce(0, (h, c) -> ((h + c) * 17) % 256);
    }

    record Lens(String label, int focalLength) { }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        Instant start = Instant.now();

        int numBoxes = 256;
        List<List<Lens>> boxes = new ArrayList<>(numBoxes);
        for (int i = 0; i < numBoxes; i++) {
            boxes.add(new ArrayList<>());
        }
        long result1 = 0;
        for (String item : input.split(",")) {
            result1 += getHash(item);
            String[] s0 = StringUtils.split(item, "-=");
            String label = s0[0];
            int boxNumber = getHash(label);
            List<Lens> box = boxes.get(boxNumber);
            if (item.endsWith("-")) {
                box.removeIf(lens -> lens.label.equals(label));
            } else {
                int focalLength = item.charAt(item.length() - 1) - '0'; // Parse last digit
                boolean replaced = false;
                for (int j = 0; j < box.size(); j++) {
                    Lens lens = box.get(j);
                    if (lens.label.equals(label)) {
                        box.set(j, new Lens(label, focalLength));
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    box.add(new Lens(label, focalLength));
                }
            }
        }

        long result2 = 0;
        for (int idxBox = 0; idxBox < boxes.size(); idxBox++) {
            List<Lens> box = boxes.get(idxBox);
            for (int idxLens = 0; idxLens < box.size(); idxLens++) {
                int focalLength = box.get(idxLens).focalLength;
                int focusingPower = (idxBox + 1) * (idxLens + 1) * focalLength;
                result2 += focusingPower;
            }
        }

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 503154) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 251353) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}

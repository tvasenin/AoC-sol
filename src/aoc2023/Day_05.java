package aoc2023;

import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.LongRangeInclusive;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day_05 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/05-test.txt"), 35, 46);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/05-main.txt"), 107430936, 23738616);


    record RangeMapping(long dstStart, long srcStart, long length) {
        public static RangeMapping of(String line) {
            String[] tokens = line.split(" ");
            return new RangeMapping(Long.parseLong(tokens[0]), Long.parseLong(tokens[1]), Long.parseLong(tokens[2]));
        }
    }

    static long applyMapping1(long val, List<RangeMapping> mapping) {
        for (RangeMapping record : mapping) {
            if ((val >= record.srcStart) && (val < record.srcStart + record.length)) {
                return val + (record.dstStart - record.srcStart);
            }
        }
        return val;
    }

    private static List<LongRangeInclusive> applyMapping2(LongRangeInclusive initialRange, List<RangeMapping> mappings) {
        List<LongRangeInclusive> ranges = new ArrayList<>();
        ranges.add(initialRange);
        List<LongRangeInclusive> result = new ArrayList<>();
        for (RangeMapping mapping : mappings) {
            LongRangeInclusive rangeFrom = LongRangeInclusive.of(mapping.srcStart, mapping.srcStart + mapping.length);
            LongRangeInclusive rangeTo = LongRangeInclusive.of(mapping.dstStart, mapping.dstStart + mapping.length);
            long shift = rangeTo.from() - rangeFrom.from();
            List<LongRangeInclusive> rangesCopy = List.copyOf(ranges);
            for (LongRangeInclusive seedRange : rangesCopy) {
                // Handle overlap
                if (rangeFrom.isOverlappedBy(seedRange)) {
                    ranges.remove(seedRange);
                    LongRangeInclusive intersection = seedRange.intersectionWith(rangeFrom);
                    result.add(intersection.shiftBy(shift));
                    if (seedRange.from() < intersection.from()) {
                        ranges.add(LongRangeInclusive.of(seedRange.from(), intersection.from()));
                    }
                    if (seedRange.to() > intersection.to()) {
                        ranges.add(LongRangeInclusive.of(intersection.to(), seedRange.to()));
                    }
                }
            }
        }
        result.addAll(ranges);
        return result;
    }

    public static TaskSolution solve(String input) {
        String[] sections = input.split("\n\n");

        String[] seedsStr = StringUtils.substringAfter(sections[0], ": ").split(" ");

        long[] seeds = Arrays.stream(seedsStr).mapToLong(Long::parseUnsignedLong).toArray();

        List<List<RangeMapping>> mappings = Arrays.stream(sections)
                .skip(1)
                .map(section -> section.lines()
                        .skip(1)
                        .map(RangeMapping::of)
                        .toList())
                .toList();

        LongStream stream1 = Arrays.stream(seeds);
        for (List<RangeMapping> mapping : mappings) {
            stream1 = stream1.map(v -> applyMapping1(v, mapping));
        }
        long result1 = stream1.min().orElseThrow();


        if (seeds.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of seeds");
        }
        int numRanges = seeds.length / 2;
        List<LongRangeInclusive> seedRanges = new ArrayList<>(numRanges);
        for (int i = 0; i < numRanges; i++) {
            long start = seeds[i * 2];
            long length = seeds[i * 2 + 1];
            seedRanges.add(LongRangeInclusive.of(start, start + length));
        }

        Stream<LongRangeInclusive> stream2 = seedRanges.stream();
        for (List<RangeMapping> mapping : mappings) {
            stream2 = stream2.flatMap(v -> applyMapping2(v, mapping).stream());
        }
        long result2 = stream2.mapToLong(LongRangeInclusive::from).min().orElseThrow();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_05::solve, true);
        Helpers.runTask(MAIN, Day_05::solve, true);
    }
}

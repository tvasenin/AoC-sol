package aoc2023;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day_19 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/19-test.txt"), 19114, 167409079868000L);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/19-main.txt"), 532551, 134343280273968L);


    // from and to are included
    private record Range(int from, int to) {

        public int size() {
            return to - from + 1;
        }

        public Pair<Range, Range> splitByRule(int split, boolean isLessSign) {
            int splitL = isLessSign ? split - 1 : split;
            int splitR = isLessSign ? split : split + 1;
            final Range matchedRange;
            final Range nonMatchedRange;
            if (splitR > from && splitL < to) {
                Range a = new Range(from, splitL);
                Range b = new Range(splitR, to);
                matchedRange = isLessSign ? a : b;
                nonMatchedRange = isLessSign ? b : a;
            } else {
                boolean isMatched = (isLessSign && to < splitR) || (!isLessSign && from > splitL);
                matchedRange = isMatched ? this : null;
                nonMatchedRange = isMatched ? null : this;
            }
            return Pair.of(matchedRange, nonMatchedRange);
        }
    }
    private record Part(Range x, Range m, Range a, Range s) { }

    // splitter splits into left (matched) and right (unmatched) pairs, each of them can be null
    private record Step(Function<Part, Pair<Part, Part>> splitter, Integer value, String targetId, String text) { }

    private static Step parseWorkflowStep(String s) {
        final Integer value;
        final String targetId;
        final Function<Part, Pair<Part, Part>> splitter;
        if (s.contains(":")) {
            String[] s0 = s.split(":");
            targetId = s0[1];
            String condStr = s0[0];
            value = Integer.parseInt(condStr, 2, condStr.length(), 10);
            char field = condStr.charAt(0);
            char op = condStr.charAt(1);
            boolean isLessSign = op == '<';
            splitter = switch (field) {
                case 'x' -> part -> {
                    Range range = part.x();
                    Pair<Range, Range> split = range.splitByRule(value, isLessSign);
                    return Pair.of(
                            split.getLeft() == null ? null : new Part(split.getLeft(), part.m(), part.a(), part.s()),
                            split.getRight() == null ? null : new Part(split.getRight(), part.m(), part.a(), part.s())
                    );
                };
                case 'm' -> part -> {
                    Range range = part.m();
                    Pair<Range, Range> split = range.splitByRule(value, isLessSign);
                    return Pair.of(
                            split.getLeft() == null ? null : new Part(part.x(), split.getLeft(), part.a(), part.s()),
                            split.getRight() == null ? null : new Part(part.x(), split.getRight(), part.a(), part.s())
                    );
                };
                case 'a' -> part -> {
                    Range range = part.a();
                    Pair<Range, Range> split = range.splitByRule(value, isLessSign);
                    return Pair.of(
                            split.getLeft() == null ? null : new Part(part.x(), part.m(), split.getLeft(), part.s()),
                            split.getRight() == null ? null : new Part(part.x(), part.m(), split.getRight(), part.s())
                    );
                };
                case 's' -> part -> {
                    Range range = part.s();
                    Pair<Range, Range> split = range.splitByRule(value, isLessSign);
                    return Pair.of(
                            split.getLeft() == null ? null : new Part(part.x(), part.m(), part.a(), split.getLeft()),
                            split.getRight() == null ? null : new Part(part.x(), part.m(), part.a(), split.getRight())
                    );
                };
                default -> throw new IllegalStateException("Unexpected field char: " + field);
            };
        } else {
            // The whole string is an unconditional target label
            targetId = s;
            value = null;
            splitter = part -> Pair.of(part, null);
        }
        return new Step(splitter, value, targetId, s);
    }

    private static void process(Part part, Map<String, List<Step>> workflows, String wfId, List<Part> acceptedParts) {
        // No loop check since input doesn't contain loops :)
        List<Step> steps = workflows.get(wfId);
        for (Step step : steps) {
            Pair<Part, Part> split = step.splitter().apply(part);
            Part matchedPart = split.getLeft();
            Part nonMatchedPart = split.getRight();
            if (matchedPart != null) {
                String targetId = step.targetId();
                if (workflows.containsKey(targetId)) {
                    process(matchedPart, workflows, targetId, acceptedParts);
                } else {
                    // Terminal ID
                    if (targetId.equals("A")) {
                        acceptedParts.add(matchedPart);
                    }
                }
            }
            if (nonMatchedPart == null) {
                return;
            } else {
                part = nonMatchedPart;
            }
        }
        throw new IllegalStateException("Last rule didn't match the input part!");
    }

    private static List<Part> process(Part part, Map<String, List<Step>> workflows) {
        List<Part> parts = new ArrayList<>();
        process(part, workflows, "in", parts);
        return parts;
    }

    public static TaskSolution solve(String input) {
        Map<String, List<Step>> workflows = new HashMap<>();
        List<Part> parts1 = new ArrayList<>();

        String[] sections = input.split("\n\n");

        for (String line : sections[0].split("\n")) {
            String[] s0 = StringUtils.split(line, "{}");
            String workflowId = s0[0];
            List<Step> steps = Pattern.compile(",").splitAsStream(s0[1]).map(Day_19::parseWorkflowStep).toList();
            workflows.put(workflowId, steps);
        }

        for (String line : sections[1].split("\n")) {
            // QnD parsing of fixed format input string
            int[] val = Arrays.stream(StringUtils.split(line, "{=,xmas}")).mapToInt(Integer::parseInt).toArray();
            Range rangeX = new Range(val[0], val[0]);
            Range rangeM = new Range(val[1], val[1]);
            Range rangeA = new Range(val[2], val[2]);
            Range rangeS = new Range(val[3], val[3]);
            parts1.add(new Part(rangeX, rangeM, rangeA, rangeS));
        }

        long result1 = parts1.stream()
                .flatMap(part -> process(part, workflows).stream())
                .mapToLong(part -> (long) part.x().from() + part.m().from() + part.a().from() + part.s().from())
                .sum();

        Range fullRange = new Range(1, 4000);
        long result2 = Stream.of(new Part(fullRange, fullRange, fullRange, fullRange))
                .flatMap(part -> process(part, workflows).stream())
                .mapToLong(part -> (long) part.x().size() * part.m().size() * part.a().size() * part.s().size())
                .sum();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_19::solve, false);
        Helpers.runTask(MAIN, Day_19::solve, false);
    }
}

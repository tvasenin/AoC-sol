package util;

public record TaskSolution(long result1, long result2) {
    public static TaskSolution of(long result1, long result2) {
        return new TaskSolution(result1, result2);
    }
}

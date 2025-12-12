package util;

public record TaskSolution(String result1, String result2) {
    public static TaskSolution of(String result1, String result2) {
        return new TaskSolution(result1, result2);
    }
    public static TaskSolution of(long result1, long result2) {
        return new TaskSolution(Long.toString(result1), Long.toString(result2));
    }
}

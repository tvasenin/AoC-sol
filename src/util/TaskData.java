package util;

public record TaskData(String input, TaskSolution result) {
    public TaskData(String input, String result1, String result2) {
        this(input, TaskSolution.of(result1, result2));
    }
    public TaskData(String input, long result1, long result2) {
        this(input, TaskSolution.of(result1, result2));
    }
}

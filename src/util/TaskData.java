package util;

public record TaskData(String input, TaskSolution result) {
    public TaskData(String input, long result1, long result2) {
        this(input, new TaskSolution(result1, result2));
    }
}

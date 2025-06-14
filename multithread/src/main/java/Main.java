import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String inputFile = "input.xlsx";//для удобства сразу уже указаны пути
        String outputStatsFile = "stats.xlsx";
        String outputTasksFile = "tasks.xlsx";
        int maxDays = 5;

        Workday simulator = new Workday(inputFile, outputStatsFile, outputTasksFile, maxDays);//конструктор, запускающий всю работу над задачами
        try {
            simulator.loadTasks();
            simulator.simulate();
        } catch (IOException e) {
            System.err.println("Ошибка работы с файлами: " + e.getMessage());
        }
    }
}
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Workday {
    private Map<String, List<Task>> employeeTasks;
    private final int maxDays;
    private final String inputFile;
    private final String outputStatsFile;
    private final String outputTasksFile;

    public Workday(String inputFile, String outputStatsFile, String outputTasksFile, int maxDays) {
        this.inputFile = inputFile;
        this.outputStatsFile = outputStatsFile;
        this.outputTasksFile = outputTasksFile;
        this.maxDays = maxDays;
    }

    //загружает задания
    public void loadTasks() throws IOException {
        employeeTasks = Io.readTasks(inputFile);
    }

    //симулирует все рабочие дни
    public void simulate() throws IOException {
        for (int day = 1; day <= maxDays; day++) {
            System.out.println("Симуляция дня " + day);

            List<Thread> threads = new ArrayList<>();
            List<Employee> employees = new ArrayList<>();

            // создаём сотрудников и потоки
            for (String empId : employeeTasks.keySet()) {
                Employee employee = new Employee(empId, employeeTasks.get(empId));
                employees.add(employee);
                Thread thread = new Thread(employee);
                threads.add(thread);
                thread.start();
            }

            // ждём завершения всех потоков
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // сбор статистики
            for (Employee emp : employees) {
                System.out.printf("Сотрудник %s: Работал %d ч, Простой %d ч, Эффективность %.2f%%\n",
                        emp.getEmployeeId(), emp.getHoursWorked(), emp.getHoursIdle(), emp.getEfficiency());
                employeeTasks.put(emp.getEmployeeId(), emp.getPendingTasks());
            }

            // сохранение данных
            Io.saveDailyStatistics(outputStatsFile.replace(".xlsx", "_day" + day + ".xlsx"), day, employees);
            Io.saveTasks(outputTasksFile, employeeTasks);

            // проверка на завершенность всех задач
            boolean allFinished = employees.stream().allMatch(Employee::isFinished);
            if (allFinished) {
                System.out.println("Все задачи завершены на день " + day);
                break;
            }
        }
    }
}
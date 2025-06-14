import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Employee implements Runnable {
    private String employeeId;
    private Queue<Task> tasks;
    private int hoursWorked;
    private int hoursIdle;
    private Task currentTask;

    public Employee(String id, List<Task> taskList) {
        this.employeeId = id;
        this.tasks = new LinkedList<>(taskList);
        this.hoursWorked = 0;
        this.hoursIdle = 0;
        if (!tasks.isEmpty()) {
            this.currentTask = tasks.poll();
        }
    }

    @Override//симулирует один рабочий день (8-ми часовой :( )
    public void run() {
        for (int hour = 0; hour < 8; hour++) {
            if (currentTask != null && !currentTask.isComplete()) {
                currentTask.workOneHour();
                hoursWorked++;
                if (currentTask.isComplete()) {
                    if (!tasks.isEmpty()) {
                        currentTask = tasks.poll();
                    } else {
                        currentTask = null;
                    }
                }
            } else {
                hoursIdle++;
            }
        }
    }

    //геттеры, ну тут все очевидно
    public List<Task> getPendingTasks() {
        List<Task> pending = new LinkedList<>();
        if (currentTask != null && !currentTask.isComplete()) {
            pending.add(currentTask);
        }
        pending.addAll(tasks);
        return pending;
    }
    public boolean isFinished() {return currentTask == null && tasks.isEmpty();}
    public String getEmployeeId() {return employeeId;}
    public int getHoursWorked() {return hoursWorked;}
    public int getHoursIdle() {return hoursIdle;}
    public double getEfficiency() {return (hoursWorked / 8.0) * 100;}
}
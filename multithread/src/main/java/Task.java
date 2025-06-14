
public class Task {
    private String taskId;
    private int baseDuration;
    private int remainingTime;
    private boolean isComplete;

    public Task(String taskId, int duration){
        this.taskId = taskId;
        baseDuration = duration;
        remainingTime = duration;
        isComplete = false;
    }

    //геттеры
    public String getTaskId(){return taskId;}
    public int getRemainingTime(){return remainingTime;}
    public int getBaseDuration(){return baseDuration;}
    public boolean isComplete(){return isComplete;}

    //работает один час
    public void workOneHour(){
        if(!isComplete && remainingTime>0){
            remainingTime--;
            if(remainingTime==0){
                isComplete = true;
            }
        }
    }


}

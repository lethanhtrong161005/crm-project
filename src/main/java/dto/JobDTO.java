package dto;
import java.util.Date;

public class JobDTO {
    private String jobName;
    private String statusName;
    private String userName;
    private String taskName;
    private Date startDate;
    private Date endDate;

    public JobDTO(String jobName, String statusName, String userName, String taskName, Date startDate, Date endDate) {
        this.jobName = jobName;
        this.statusName = statusName;
        this.userName = userName;
        this.taskName = taskName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getJobName() {
        return jobName;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getUserName() {
        return userName;
    }

    public String getTaskName() {
        return taskName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}

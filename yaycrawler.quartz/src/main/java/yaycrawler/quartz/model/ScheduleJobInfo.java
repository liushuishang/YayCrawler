package yaycrawler.quartz.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class ScheduleJobInfo implements Serializable {
    /**
     * 任务id
     */
    private String jobId;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务分组
     */
    private String jobGroup;
    /**
     * 任务状态 0禁用 1启用 2删除
     */
    private String jobStatus;
    /**
     * 任务运行时间表达式
     */
    private String cronExpression;
    /**
     * 任务描述
     */
    private String description;


    public ScheduleJobInfo() {
        jobId = UUID.randomUUID().toString();
    }

    public ScheduleJobInfo(String jobName, String jobGroup, String cronExpression) {
        this();
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.cronExpression = cronExpression;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public String toString() {
        return "ScheduleJobInfo{" +
                "jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", desc='" + description + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

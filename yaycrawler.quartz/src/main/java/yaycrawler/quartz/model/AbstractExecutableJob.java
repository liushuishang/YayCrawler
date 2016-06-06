package yaycrawler.quartz.model;

import java.io.Serializable;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public abstract class AbstractExecutableJob implements IExecutable,Serializable {
    private ScheduleJobInfo jobInfo;

    public AbstractExecutableJob(ScheduleJobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }


    public ScheduleJobInfo getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(ScheduleJobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }

    public String getJobName() {
        return jobInfo.getJobName();
    }

    public String getJobGroup() {
        return jobInfo.getJobGroup();
    }

    public String getCronExpression() {
        return jobInfo.getCronExpression();
    }

}

package yaycrawler.quartz.model;

import org.quartz.JobExecutionContext;


/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class SimpleExecutableJob extends AbstractExecutableJob  {

    public SimpleExecutableJob(ScheduleJobInfo jobInfo) {
        super(jobInfo);
    }

    @Override
    public boolean execute(JobExecutionContext context) {
        /**
         * 这里做一些操作
         */
        System.out.println(this.toString());
        return true;
    }
}

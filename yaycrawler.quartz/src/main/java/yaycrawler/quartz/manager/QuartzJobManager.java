package yaycrawler.quartz.manager;

import yaycrawler.quartz.dao.QuartzDao;
import yaycrawler.quartz.model.ScheduleJobInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class QuartzJobManager {

    private QuartzDao quartzDao;


    /** 计划任务map  实际应该保存到数据库*/
    private static Map<String, ScheduleJobInfo> jobMap = new HashMap<String, ScheduleJobInfo>();
    static {
        for (int i = 0; i < 5; i++) {
            ScheduleJobInfo job = new ScheduleJobInfo();
            job.setJobId("10001" + i);
            job.setJobName("data_import" + i);
            job.setJobGroup("dataWork");
            job.setJobStatus("1");
            job.setCronExpression("0/5 * * * * ?");
            job.setDescription("数据导入任务");
            addJobToStore(job);
        }
    }

    /**
     * 添加任务
     * @param scheduleJobInfo
     */
    public static void addJobToStore(ScheduleJobInfo scheduleJobInfo) {
        jobMap.put(scheduleJobInfo.getJobGroup() + "_" + scheduleJobInfo.getJobName(), scheduleJobInfo);
    }


    public Collection<ScheduleJobInfo> getAllScheduleJob()
    {
        return jobMap.values();
    }




}

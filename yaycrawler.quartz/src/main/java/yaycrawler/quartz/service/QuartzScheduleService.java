package yaycrawler.quartz.service;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yaycrawler.quartz.model.AbstractExecutableJob;
import yaycrawler.quartz.model.Constant;
import yaycrawler.quartz.model.ScheduleJobInfo;
import yaycrawler.quartz.quartz.QuartzJobFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
@Service
public class QuartzScheduleService {

    private static Logger logger = LoggerFactory.getLogger(QuartzScheduleService.class);

    @Autowired
    private Scheduler scheduler;

    /**
     * 添加一个Job
     *
     * @param job
     */
    public boolean addJob(AbstractExecutableJob job) {
        try {
            //获取TriggerKey
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            //从数据库中查询触发器
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //不存在则新建一个触发器
            if (null == trigger) {
                JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
                        .withIdentity(job.getJobName(), job.getJobGroup()).storeDurably(true)
                        .build();
                jobDetail.getJobDataMap().put(Constant.JOB_PARAM_KEY, job);
                //表达式调度构建器
                CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                //按新的cronExpression表达式构建一个新的trigger
                trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(cronBuilder).withDescription(job.getJobInfo().getDescription()).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                // Trigger已存在，那么更新相应的定时设置
                //表达式调度构建器
                CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                //按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronBuilder).withDescription(job.getJobInfo().getDescription()).build();
                //按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);
            }
            return true;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * 更新一个job
     *
     * @param jobInfo
     * @return
     */
    public boolean updateJob(ScheduleJobInfo jobInfo) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobInfo.getJobName(), jobInfo.getJobGroup());

            JobDetail jobDetail = scheduler.getJobDetail(getJobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            //jobDetail = jobDetail.getJobBuilder().ofType(jobClass).build();
            //更新参数 实际测试中发现无法更新
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.put(Constant.JOB_PARAM_KEY, jobInfo);
            jobDetail.getJobBuilder().usingJobData(jobDataMap);

            //获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //表达式调度构建器
            CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression());
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withDescription(jobInfo.getDescription())
                    .withSchedule(cronBuilder).build();
            //按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }


    /**
     * 获取计划中的job
     *
     * @return
     */
    public List<ScheduleJobInfo> getScheduledJobList() {
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            List<ScheduleJobInfo> jobList = new ArrayList<ScheduleJobInfo>();
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    ScheduleJobInfo job = new ScheduleJobInfo();
                    job.setJobName(jobKey.getName());
                    job.setJobGroup(jobKey.getGroup());
                    job.setDescription(trigger.getDescription());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    job.setJobStatus(triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        job.setCronExpression(cronExpression);
                    }
                    jobList.add(job);
                }
            }
            return jobList;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * 获取运行中的任务
     *
     * @return
     */
    public List<ScheduleJobInfo> getRunningJobList() {
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            List<ScheduleJobInfo> jobList = new ArrayList<ScheduleJobInfo>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                ScheduleJobInfo job = new ScheduleJobInfo();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                job.setJobName(jobKey.getName());
                job.setJobGroup(jobKey.getGroup());
                job.setDescription(trigger.getDescription());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
            return jobList;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * 暂停一个Job
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public boolean pauseJob(String jobName, String jobGroup) {
        try {
            JobKey jobKey = getJobKey(jobName, jobGroup);
            scheduler.pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 恢复一个暂停的作业
     * @param jobName
     * @param jobGroup
     * @return
     */
    public boolean resumeJob(String jobName, String jobGroup) {
        try {
            JobKey jobKey = getJobKey(jobName, jobGroup);
            scheduler.resumeJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * 删除一个Job
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public boolean deleteJob(String jobName, String jobGroup) {
        try {
            JobKey jobKey = getJobKey(jobName, jobGroup);
            scheduler.deleteJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }




    private JobKey getJobKey(String jobName, String jobGroup) {
        return JobKey.jobKey(jobName, jobGroup);
    }

}

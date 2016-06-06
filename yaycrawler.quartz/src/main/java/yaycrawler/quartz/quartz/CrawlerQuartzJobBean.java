package yaycrawler.quartz.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import yaycrawler.quartz.service.SimpleService;

/**
 * Created by ucs_yuananyun on 2016/6/3.
 */
public class CrawlerQuartzJobBean extends QuartzJobBean{
    private static final Logger logger = LoggerFactory.getLogger(CrawlerQuartzJobBean.class);

    @Override
    protected void executeInternal(JobExecutionContext jobexecutioncontext) throws JobExecutionException {
        Trigger trigger = jobexecutioncontext.getTrigger();
        String triggerName = trigger.getKey().getName();


        /**
         *
         */

        SimpleService simpleService = getApplicationContext(jobexecutioncontext).getBean("simpleService",
                SimpleService.class);
        simpleService.testMethod(triggerName);

    }

    private ApplicationContext getApplicationContext(final JobExecutionContext jobexecutioncontext) {
        try {
            return (ApplicationContext) jobexecutioncontext.getScheduler().getContext().get("applicationContextKey");
        } catch (SchedulerException e) {
            logger.error("jobexecutioncontext.getScheduler().getContext() error!", e);
            throw new RuntimeException(e);
        }
    }
}

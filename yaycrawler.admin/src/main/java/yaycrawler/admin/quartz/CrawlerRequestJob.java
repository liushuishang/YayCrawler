package yaycrawler.admin.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import yaycrawler.admin.communication.MasterActor;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.quartz.model.AbstractExecutableJob;
import yaycrawler.quartz.model.ScheduleJobInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class CrawlerRequestJob extends AbstractExecutableJob {
    private static Logger logger = LoggerFactory.getLogger(CrawlerRequestJob.class);
    private List<CrawlerRequest> crawlerRequestList;

    public CrawlerRequestJob(ScheduleJobInfo jobInfo) {
        super(jobInfo);
    }

    @Override
    public boolean execute(JobExecutionContext context) {
        //发布任务到Master
        try {
            ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContextKey");
            MasterActor masterActor = appContext.getBean("masterActor", MasterActor.class);
            if (masterActor == null) throw new RuntimeException("获取MasterActor失败！");
            return masterActor.publishTasks(crawlerRequestList.toArray(new CrawlerRequest[]{}));
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public List<CrawlerRequest> getCrawlerRequestList() {
        return crawlerRequestList;
    }

    public void setCrawlerRequestList(List<CrawlerRequest> crawlerRequestList) {
        this.crawlerRequestList = crawlerRequestList;
    }

    public void addCrawlerRequest(CrawlerRequest... requests) {
        if (crawlerRequestList == null) crawlerRequestList = new LinkedList<>();
        for (CrawlerRequest crawlerRequest : requests) {
            crawlerRequestList.add(crawlerRequest);
        }
    }

    @Override
    public String toString() {
        return "CrawlerRequestJob: " + this.getJobInfo().toString();
    }
}

package yaycrawler.admin.quartz;

import org.quartz.JobExecutionContext;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.quartz.model.AbstractExecutableJob;
import yaycrawler.quartz.model.ScheduleJobInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class CrawlerRequestJob extends AbstractExecutableJob {

    private List<CrawlerRequest> crawlerRequestList;

    public CrawlerRequestJob(ScheduleJobInfo jobInfo) {
        super(jobInfo);
    }

    @Override
    public boolean execute(JobExecutionContext context) {
        return false;
    }

    public List<CrawlerRequest> getCrawlerRequestList() {
        return crawlerRequestList;
    }

    public void setCrawlerRequestList(List<CrawlerRequest> crawlerRequestList) {
        this.crawlerRequestList = crawlerRequestList;
    }
    public void addCrawlerRequest(CrawlerRequest request)
    {
        if(crawlerRequestList==null) crawlerRequestList=new LinkedList<>();
        crawlerRequestList.add(request);
    }
}

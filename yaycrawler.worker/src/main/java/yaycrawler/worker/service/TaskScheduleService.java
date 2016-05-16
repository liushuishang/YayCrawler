package yaycrawler.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.spider.crawler.YaySpider;
import yaycrawler.spider.pipeline.GenericPipeline;
import yaycrawler.spider.processor.GenericPageProcessor;
import yaycrawler.spider.scheduler.CrawlerQueueScheduler;
import yaycrawler.spider.service.PageSiteService;
import yaycrawler.spider.utils.RequestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Service
public class TaskScheduleService {

    @Autowired
    private PageSiteService pageSiteService;
    @Autowired
    private GenericPageProcessor pageProcessor;
    @Autowired
    private GenericPipeline pipeline;

    @Value("${worker.spider.maxIdleTime}")
    private long maxIdleTime;


    private Map<String, YaySpider> spiderMap = new HashMap<>();

    public TaskScheduleService() {
    }

    public void doSchedule(List<CrawlerRequest> taskList) {
        for (CrawlerRequest CrawlerRequest : taskList) {
            String domain = CrawlerRequest.getDomain();
            YaySpider spider = spiderMap.get(domain);
            if (spider == null)
                spider = createSpider(domain);
            spider.addRequest(convertCrawlerRequestToSpiderRequest(CrawlerRequest));
            if (spider.getStatus() != Spider.Status.Running)
                spider.runAsync();
        }
    }

    private Request convertCrawlerRequestToSpiderRequest(CrawlerRequest CrawlerRequest) {
        return RequestHelper.createRequest(CrawlerRequest.getUrl(), CrawlerRequest.getMethod().toUpperCase(), CrawlerRequest.getData());
    }

    private YaySpider createSpider(String domain) {
        Site site = pageSiteService.getSite(domain);
        YaySpider spider = new YaySpider(domain, site, pageProcessor);
        spider.setScheduler(new CrawlerQueueScheduler());
        spider.addPipeline(pipeline);
        spiderMap.put(domain, spider);
        return spider;
    }


}

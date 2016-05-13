package yaycrawler.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import yaycrawler.common.model.CrawRequest;
import yaycrawler.spider.crawler.YaySpider;
import yaycrawler.spider.processor.GenericPageProcessor;
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


    private Map<String, YaySpider> spiderMap = new HashMap<>();

    public TaskScheduleService() {
    }

    public void doSchedule(List<CrawRequest> taskList) {
        for (CrawRequest crawRequest : taskList) {
            String domain = crawRequest.getDomain();
            YaySpider spider = spiderMap.get(domain);
            if (spider == null || spider.getStatus() != Spider.Status.Running)
                spider = createAndStartSpider(domain);
            spider.addRequest(convertCrawRequestToSpiderRequest(crawRequest));
        }
    }

    private Request convertCrawRequestToSpiderRequest(CrawRequest crawRequest) {
        return RequestHelper.createRequest(crawRequest.getUrl(), crawRequest.getMethod().toUpperCase(), crawRequest.getData());
    }

    private YaySpider createAndStartSpider(String domain) {
        Site site = pageSiteService.getSite(domain);
        YaySpider spider = new YaySpider(domain, site, pageProcessor);
        spider.setScheduler(new QueueScheduler());
        spiderMap.put(domain, spider);
        spider.start();
        return spider;
    }

}

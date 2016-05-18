package yaycrawler.worker.service;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import yaycrawler.worker.listener.TaskFailureListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Service
public class TaskScheduleService {
    private static Logger logger = LoggerFactory.getLogger(TaskScheduleService.class);

    @Autowired
    private PageSiteService pageSiteService;
    @Autowired
    private GenericPageProcessor pageProcessor;
    @Autowired
    private GenericPipeline pipeline;

    @Autowired
    private TaskFailureListener failureListener;

    @Value("${worker.spider.maxIdleTime}")
    private long maxIdleTime;


    private Map<String, YaySpider> spiderMap = new HashMap<>();

    public TaskScheduleService() {
    }

    public Integer getRunningTaskCount() {
        int count = 0;
        for (Map.Entry<String, YaySpider> entry : spiderMap.entrySet()) {
            CrawlerQueueScheduler crawlerQueueScheduler = (CrawlerQueueScheduler)entry.getValue().getScheduler();
            count += crawlerQueueScheduler.getLeftRequestsCount(null);
        }
        return count;
    }

    public void doSchedule(List<CrawlerRequest> taskList) {
        try {
            logger.info("worker接收到{}个任务", JSON.toJSON(taskList));
            for (CrawlerRequest CrawlerRequest : taskList) {
                String domain = CrawlerRequest.getDomain();
                YaySpider spider = spiderMap.get(domain);
                if (spider == null)
                    spider = createSpider(domain);
                spider.addRequest(convertCrawlerRequestToSpiderRequest(CrawlerRequest));
                if (spider.getStatus() != Spider.Status.Running)
                    spider.runAsync();
            }
            logger.info("worker任务分配完成！");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private Request convertCrawlerRequestToSpiderRequest(CrawlerRequest CrawlerRequest) {
        return RequestHelper.createRequest(CrawlerRequest.getUrl(), CrawlerRequest.getMethod().toUpperCase(), CrawlerRequest.getData());
    }

    private YaySpider createSpider(String domain) {
        YaySpider spider = new YaySpider(domain, pageSiteService, pageProcessor);
        spider.setScheduler(new CrawlerQueueScheduler());
        spider.addPipeline(pipeline);
        spider.getSpiderListeners().add(failureListener);
        spiderMap.put(domain, spider);
        return spider;
    }


}

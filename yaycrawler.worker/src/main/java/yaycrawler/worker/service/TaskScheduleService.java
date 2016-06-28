package yaycrawler.worker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.crawler.YaySpider;
import yaycrawler.spider.downloader.GenericCrawlerDownLoader;
import yaycrawler.spider.listener.IPageParseListener;
import yaycrawler.spider.pipeline.GenericPipeline;
import yaycrawler.spider.processor.GenericPageProcessor;
import yaycrawler.spider.scheduler.CrawlerQueueScheduler;
import yaycrawler.spider.service.PageSiteService;
import yaycrawler.spider.utils.RequestHelper;
import yaycrawler.worker.listener.TaskDownloadFailureListener;

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
    private PageParserRuleService pageParserRuleService;

    @Autowired
    private GenericPageProcessor pageProcessor;
    @Autowired
    private GenericPipeline pipeline;

    @Autowired
    private GenericCrawlerDownLoader genericCrawlerDownLoader;

    @Autowired
    private TaskDownloadFailureListener downloadFailureListener;

    @Autowired
    private IPageParseListener pageParseListener;

    @Value("${worker.spider.threadCount}")
    private int spiderThreadCount;

    private Map<String, YaySpider> spiderMap = new HashMap<>();

    public TaskScheduleService() {
    }

    public void refreshSpiderSite(String domain) {
        YaySpider spider = spiderMap.get(domain);
        if (spider == null) return;
        Site newSite = pageSiteService.getSite(domain);
        spider.setSite(newSite);
    }


    public Integer getRunningTaskCount() {
        int count = 0;
        for (Map.Entry<String, YaySpider> entry : spiderMap.entrySet()) {
            CrawlerQueueScheduler crawlerQueueScheduler = (CrawlerQueueScheduler) entry.getValue().getScheduler();
            count += crawlerQueueScheduler.getLeftRequestsCount(null);
        }
        logger.info("worker还有{}个运行中任务", count);
        return count;
    }

    public void doSchedule(List<CrawlerRequest> taskList) {
        try {
            logger.info("worker接收到{}个任务", taskList.size());
            for (CrawlerRequest crawlerRequest : taskList) {
                if(crawlerRequest==null) continue;
                //如果查找不到与url相关的解析规则，则该任务不能执行
                if (pageParserRuleService.findOnePageInfoByRgx(crawlerRequest.getUrl()) == null) {
                    logger.info("查找不到与{}匹配的解析规则，该任务失败！", crawlerRequest.getUrl());
                    pageParseListener.onError(convertCrawlerRequestToSpiderRequest(crawlerRequest), "查找不到匹配的页面解析规则！");
                    continue;
                }
                String domain = crawlerRequest.getDomain();
                YaySpider spider = spiderMap.get(domain);
                if (spider == null)
                    spider = createSpider(domain);
                spider.addRequest(convertCrawlerRequestToSpiderRequest(crawlerRequest));
                if (spider.getStatus() != Spider.Status.Running)
                    spider.runAsync();
            }
            logger.info("worker任务分配完成！");
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
    }

    private Request convertCrawlerRequestToSpiderRequest(CrawlerRequest CrawlerRequest) {
        return RequestHelper.createRequest(CrawlerRequest.getUrl(), CrawlerRequest.getMethod().toUpperCase(), CrawlerRequest.getData());
    }

    private YaySpider createSpider(String domain) {
        YaySpider spider = new YaySpider(domain, pageSiteService, pageProcessor);
        spider.setScheduler(new CrawlerQueueScheduler());
        spider.thread(spiderThreadCount);
        spider.addPipeline(pipeline);
        spider.setDownloader(genericCrawlerDownLoader);
        spider.getSpiderListeners().add(downloadFailureListener);
        spiderMap.put(domain, spider);
        return spider;
    }


    /**
     * 中断Worker的所有任务
     */
    public void interruptAllTasks() {
        logger.info("Worker开始停止所有的爬虫……");
        for (YaySpider spider : spiderMap.values()) {
            try {
                spider.stop();
                spider.close();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        logger.info("Worker的所有爬虫停止完成");
    }
}

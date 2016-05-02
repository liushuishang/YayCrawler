package yaycrawler.spider.service;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import yaycrawler.spider.crawler.YaySpider;

import java.util.UUID;

/**
 * Created by yuananyun on 2016/4/30.
 */
public class Spider0731Service extends BaseSpiderService {

    public Spider0731Service(Site site, Scheduler scheduler, Downloader downloader, PageProcessor pageProcessor, Pipeline... pipelines) {
        spider = new YaySpider(site, pageProcessor);
        spider.scheduler(scheduler);
        spider.setDownloader(downloader);
        spider.setExitWhenComplete(false);
        spider.setUUID(UUID.randomUUID().toString());
        for (Pipeline pipeline : pipelines) {
            spider.addPipeline(pipeline);
        }
    }

    public void start(boolean isAsync, String... urls) {
        spider.addUrl(urls);
        if (isAsync)
            spider.runAsync();
        else spider.run();
    }

    @Override
    public String getDomain() {
        return "floor.0731fdc.com";
    }
}

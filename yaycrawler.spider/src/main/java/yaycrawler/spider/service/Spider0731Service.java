package yaycrawler.spider.service;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.UUID;

/**
 * Created by yuananyun on 2016/4/30.
 */
//@Service
public class Spider0731Service {

    private Spider spider;
    public Spider0731Service(Scheduler scheduler, Downloader downloader, PageProcessor pageProcessor, Pipeline ... pipelines) {
        spider = Spider.create(pageProcessor)
                .scheduler(scheduler)
                .setDownloader(downloader)
                .setExitWhenComplete(false)
                .setUUID(UUID.randomUUID().toString());
        for (Pipeline pipeline : pipelines) {
            spider.addPipeline(pipeline);
        }
    }

    public void addUrls(String ... urls) {
        spider.addUrl(urls);
    }

    public void start(boolean isAsync) {
        if (isAsync)
            spider.runAsync();
        else spider.run();
    }
}

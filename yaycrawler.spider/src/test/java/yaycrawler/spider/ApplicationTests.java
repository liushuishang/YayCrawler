package yaycrawler.spider;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import yaycrawler.common.service.PageParserRuleService;
import yaycrawler.common.service.PageSiteService;
import yaycrawler.spider.processor.GenericPageProcessor;
import yaycrawler.spider.service.Spider0731Service;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ApplicationTests {

    @Autowired
    private PageParserRuleService parserRuleService;
    @Autowired
    private PageSiteService pageSiteService;

    @Test
    public void contextLoads() {
    }

    @Test
    @Ignore
    public void initDB()
    {
        parserRuleService.getPageRegionList("http://floor.0731fdc.com/jggs.php");
    }


    @Test
    public void test0731() {

        String seedUrl = "http://floor.0731fdc.com/jggs.php";
        GenericPageProcessor pageProcessor = new GenericPageProcessor(parserRuleService);

        Downloader downloader = new HttpClientDownloader();
        Pipeline pipeline = new ConsolePipeline();
        Scheduler scheduler = new RedisScheduler("localhost");

        Site site = pageSiteService.getSite("floor.0731fdc.com");

        Spider0731Service spider0731Service = new Spider0731Service(site, scheduler, downloader, pageProcessor, pipeline);
        spider0731Service.start(false, seedUrl);

    }

}

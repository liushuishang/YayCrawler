package yaycrawler.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.pipeline.GenericPipeline;
import yaycrawler.spider.processor.GenericPageProcessor;
import yaycrawler.spider.service.PageSiteService;
import yaycrawler.spider.service.Spider0731Service;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ApplicationTests {

	@Autowired
	private GenericPipeline genericPipeline;

	@Autowired
	private PageParserRuleService parserRuleService;
	@Autowired
	private PageSiteService pageSiteService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void testMongoDB()
	{
		Map<String, Object> data = new HashMap<>();
		data.put("name", "zhangsan");
		data.put("age", 28);
		mongoTemplate.save(data, "yay");
	}


	@Test
	public void testSpider()
	{
		String seedUrl = "http://floor.0731fdc.com/jggs.php";
		GenericPageProcessor pageProcessor = new GenericPageProcessor(parserRuleService);

		Downloader downloader = new HttpClientDownloader();
		Scheduler scheduler =new QueueScheduler();

		Site site = pageSiteService.getSite("floor.0731fdc.com");

		Spider0731Service spider0731Service = new Spider0731Service(site, scheduler, downloader, pageProcessor, genericPipeline);
		spider0731Service.start(false, seedUrl);
	}


}

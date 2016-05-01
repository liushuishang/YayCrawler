package yaycrawler.spider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedCaseInsensitiveMap;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import yaycrawler.spider.processor.DetailPageProcessor;
import yaycrawler.spider.processor.GenericPageProcessor;
import yaycrawler.spider.processor.ListPageProcessor;
import yaycrawler.spider.service.Spider0731Service;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}


	@Test
	public void test0731()
	{
		String seedUrl = "http://floor.0731fdc.com/jggs.php";
		//改成从数据库中读取
		String domain = "floor.0731fdc.com";
		Site site = Site.me().setDomain(domain)
				.setCharset("GBK")
				.setRetrySleepTime(1000)
				.setSleepTime(500)
				.setTimeOut(10000)
				.setUseGzip(true)
				.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36")
				.addHeader("Host", domain)
				.addHeader("Accept-Language", "zh-CN,zh;q=0.8");

		Map<String, PageProcessor> urlPatternAndProcessorMap = new LinkedCaseInsensitiveMap<>();
		urlPatternAndProcessorMap.put(".*/jggs.php.*",new ListPageProcessor());
		urlPatternAndProcessorMap.put(".*/info.php?.+",new DetailPageProcessor());

		GenericPageProcessor pageProcessor = new GenericPageProcessor(site, urlPatternAndProcessorMap);
		Downloader downloader = new HttpClientDownloader();
		Pipeline pipeline=new ConsolePipeline();
		Scheduler scheduler=new RedisScheduler("localhost");

		Spider0731Service spider0731Service = new Spider0731Service(scheduler, downloader, pageProcessor, pipeline);
		spider0731Service.addUrls(seedUrl);
		spider0731Service.start(false);

	}

}

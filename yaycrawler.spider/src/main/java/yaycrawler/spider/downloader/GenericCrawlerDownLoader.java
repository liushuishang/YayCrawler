package yaycrawler.spider.downloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.service.PageParserRuleService;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
@Component
public class GenericCrawlerDownLoader extends AbstractDownloader {
    @Autowired
    private PageParserRuleService pageParserRuleService;

    private CrawlerHttpClientDownloader httpClientDownloader;
    private PhantomJsMockDonwnloader mockDonwnloader;


    public GenericCrawlerDownLoader() {
        httpClientDownloader = new CrawlerHttpClientDownloader();
        mockDonwnloader = new PhantomJsMockDonwnloader();
    }


    @Override
    public Page download(Request request, Task task) {
        //记录当前请求使用的Cookie
        request.putExtra("cookieIds", task.getSite().getCookies().keySet());

        PageInfo pageInfo = pageParserRuleService.findOnePageInfoByRgx(request.getUrl());
        if(pageInfo == null)
            return httpClientDownloader.download(request, task);;
        if ("1".equals(pageInfo.getIsJsRendering())) {
            request.putExtra("$pageInfo",pageInfo);
            return mockDonwnloader.download(request, task);
        }
        else
            return httpClientDownloader.download(request, task);
    }

    @Override
    public void setThread(int threadNum) {
        httpClientDownloader.setThread(threadNum);
        mockDonwnloader.setThread(threadNum);
    }
}

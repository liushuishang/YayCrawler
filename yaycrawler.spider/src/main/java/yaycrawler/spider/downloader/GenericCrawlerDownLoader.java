package yaycrawler.spider.downloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.service.PageParserRuleService;

import java.util.regex.Pattern;

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

    private static Pattern redirectPattern = Pattern.compile("<script>window.location.href='.*';</script>");
    @Override
    public Page download(Request request, Task task) {
        //记录当前请求使用的Cookie
        request.putExtra("cookieIds", task.getSite().getCookies().keySet());
        Page page;
        PageInfo pageInfo = pageParserRuleService.findOnePageInfoByRgx(request.getUrl());
        if(pageInfo == null)
            page= httpClientDownloader.download(request, task);
        else if ("1".equals(pageInfo.getIsJsRendering()))
            page= mockDonwnloader.download(request, task);
        else
            page= httpClientDownloader.download(request, task);

        if(redirectPattern.matcher( page.getRawText()).matches())
            page=mockDonwnloader.download(request, task);
        return page;
    }

    @Override
    public void setThread(int threadNum) {
        httpClientDownloader.setThread(threadNum);
        mockDonwnloader.setThread(threadNum);
    }
}

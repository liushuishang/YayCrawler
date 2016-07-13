package yaycrawler.spider.downloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.domain.SiteCookie;
import yaycrawler.dao.service.PageCookieService;
import yaycrawler.dao.service.PageParserRuleService;

import java.util.regex.Pattern;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
@Component
public class GenericCrawlerDownLoader extends AbstractDownloader {

//    private static Logger logger = LoggerFactory.getLogger(GenericCrawlerDownLoader.class);

    @Autowired
    private PageParserRuleService pageParserRuleService;

    @Autowired
    private PageCookieService pageCookieService;

    private CrawlerHttpClientDownloader httpClientDownloader;
    private PhantomJsMockDonwnloader mockDonwnloader;


    public GenericCrawlerDownLoader() {
        httpClientDownloader = new CrawlerHttpClientDownloader();
        mockDonwnloader = new PhantomJsMockDonwnloader();
    }

    private static Pattern redirectPattern = Pattern.compile("<script.*(?s).*location.href\\s*=.*(?s).*</script>");

    @Override
    public Page download(Request request, Task task) {
        PageInfo pageInfo = pageParserRuleService.findOnePageInfoByRgx(request.getUrl());
        boolean isJsRendering = pageInfo != null && "1".equals(pageInfo.getIsJsRendering());
        SiteCookie siteCookie = pageCookieService.getCookieByUrl(request.getUrl());
        String cookie =null;
        if(siteCookie!=null) {
            cookie=siteCookie.getCookie();
            String cookieId = siteCookie.getId();
            request.putExtra("cookieId", cookieId);
        }
        Page page = !isJsRendering ? httpClientDownloader.download(request, task, cookie) : mockDonwnloader.download(request, task, cookie);
        if ((!"post".equalsIgnoreCase(request.getMethod())&&page != null) && redirectPattern.matcher(page.getRawText()).find())
            page = mockDonwnloader.download(request, task, cookie);
        return page;
    }

    @Override
    public void setThread(int threadNum) {
        httpClientDownloader.setThread(threadNum);
        mockDonwnloader.setThread(threadNum);
    }
}

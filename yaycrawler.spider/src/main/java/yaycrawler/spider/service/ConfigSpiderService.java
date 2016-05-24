package yaycrawler.spider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.utils.UrlUtils;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.dao.domain.PageParseRegion;
import yaycrawler.spider.downloader.CrawlerHttpClientDownloader;
import yaycrawler.spider.processor.GenericPageProcessor;

import java.util.*;

/**
 * Created by ucs_yuananyun on 2016/5/4.
 */
@Service
public class ConfigSpiderService {

    private Downloader downloader;
    private GenericPageProcessor pageProcessor;

    @Autowired
    private PageSiteService pageSiteService;

    public ConfigSpiderService() {
        downloader = new CrawlerHttpClientDownloader();
        downloader.setThread(1);
        pageProcessor = new GenericPageProcessor();
    }

    /**
     * 测试一个页面区域解析规则
     *
     * @param request
     * @param parseRegion
     * @param page
     * @return
     */
    public Map<String, Object> test(final Request request, PageParseRegion parseRegion, Page page, Site site) {
        if (pageProcessor == null) return null;

        if (page == null) {
            final Site finalSite = site;
            page = downloader.download(request, new Task() {
                @Override
                public String getUUID() {
                    return UUID.randomUUID().toString();
                }

                @Override
                public Site getSite() {
                    return finalSite == null ? ConfigSpiderService.this.getSite(request.getUrl()) : finalSite;
                }
            });
        }
        if (page == null) return null;
        List<CrawlerRequest> childRequestList=new LinkedList<>();
        Map<String, Object> data = pageProcessor.parseOneRegion(page, parseRegion,childRequestList);
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("childRequests", childRequestList);
        return result;
    }

    private Site getSite(String url) {
        Site site = pageSiteService.getSite(url);
        if (site == null) {
            site = Site.me();
            String domain = UrlUtils.getDomain(url);
            site.setDomain(domain);
            site.addHeader("host", domain);
        }
        return site;
    }

}

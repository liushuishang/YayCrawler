package yaycrawler.spider.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.domain.PageParseRegion;
import yaycrawler.spider.downloader.GenericCrawlerDownLoader;
import yaycrawler.spider.processor.GenericPageProcessor;
import yaycrawler.spider.resolver.SelectorExpressionResolver;
import yaycrawler.spider.utils.RequestHelper;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by ucs_yuananyun on 2016/5/4.
 */
@Service
public class ConfigSpiderService {

    @Autowired
    private GenericCrawlerDownLoader downloader;
    @Autowired
    private GenericPageProcessor pageProcessor;

    @Autowired
    private PageSiteService pageSiteService;

    public ConfigSpiderService() {

    }

    /**
     * 测试一个页面区域解析规则
     *
     * @param crawlerRequest
     * @param parseRegion
     * @param page
     * @return
     */
    public Map<String, Object> testRegionRule(CrawlerRequest crawlerRequest, PageParseRegion parseRegion, Page page, Site site) {
        final Request request = RequestHelper.createRequest(crawlerRequest.getUrl(), crawlerRequest.getMethod(), crawlerRequest.getData());
        if (pageProcessor == null || request == null) return null;
        if (page == null) {
            final Site finalSite = site;
            page = downloadPage(request, finalSite);
        }
        if (page == null) return null;
        List<CrawlerRequest> childRequestList = new LinkedList<>();
        Map<String, Object> data = pageProcessor.parseOneRegion(page, parseRegion, childRequestList);
        Map<String, Object> result = new HashMap<>();
        if (data != null)
            result.put("data", data);
        if (childRequestList.size() > 0)
            result.put("childRequests", childRequestList);
        return result;
    }


    public Object testExpressionOnPage(PageInfo pageInfo, String expression) {
        if (StringUtils.isBlank(expression)) return null;
        String targetUrl = pageInfo.getPageUrl();
        Map<String, Object> paramsMap = pageInfo.getParamsMap();
        Request request = RequestHelper.createRequest(targetUrl, pageInfo.getMethod(), paramsMap);
        Page page = downloadPage(request, null);
        if (page == null) return RestFulResult.failure("页面下载失败！");
        pageProcessor.process(page);
        if (!pageProcessor.pageValidated(page, pageInfo.getPageValidationRule())) {
            pageMap.remove(request.getUrl());
            return RestFulResult.failure("页面内容不正确!");
        }

        Object result = null;
        if (expression.toLowerCase().contains("getjson()"))
            result = SelectorExpressionResolver.resolve(request, page.getJson(), expression);
        else
            result = SelectorExpressionResolver.resolve(request, page.getHtml(), expression);
        if (result instanceof Selectable) {
            final StringBuilder sb = new StringBuilder();
            ((Selectable) result).all().forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    sb.append(s);
                }
            });
            return sb.toString();
        }
        if (result instanceof String) return result;
        return JSON.toJSONString(result);
    }


    private Map<String, Map<String, Object>> pageMap = new HashMap<>();

    private Page downloadPage(final Request request, final Site finalSite) {
        if (pageMap.containsKey(request.getUrl())) {
            Map<String, Object> m = pageMap.get(request.getUrl());
            Long inputTime = MapUtils.getLong(m, "inputTime");
            Page page = (Page) MapUtils.getObject(m, "page");
            if (System.currentTimeMillis() - inputTime > 86400000) pageMap.remove(m);
            return page;
        }
        Page page = downloader.download(request, new Task() {
            @Override
            public String getUUID() {
                return UUID.randomUUID().toString();
            }

            @Override
            public Site getSite() {
                return finalSite == null ? ConfigSpiderService.this.getSite(request.getUrl()) : finalSite;
            }
        });
        if (page != null) {
//            Map<String, Object> m = new HashMap<>();
//            m.put("inputTime", System.currentTimeMillis());
//            m.put("page", page);
//            pageMap.put(request.getUrl(), m);
        }
        return page;
    }

    private Site getSite(String url) {
        return pageSiteService.getSite(UrlUtils.getDomain(url));
    }

}

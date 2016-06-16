package yaycrawler.spider.processor;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.dao.domain.*;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.listener.IPageParseListener;
import yaycrawler.spider.resolver.SelectorExpressionResolver;
import yaycrawler.spider.service.PageSiteService;

import java.util.*;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Component(value="genericPageProcessor")
public class GenericPageProcessor implements PageProcessor {
    private static Logger logger = LoggerFactory.getLogger(GenericPageProcessor.class);
    @Autowired(required = false)
    private IPageParseListener pageParseListener;

    @Autowired
    private PageParserRuleService pageParserRuleService;

    @Autowired
    private PageSiteService pageSiteService;


    private static String DEFAULT_PAGE_SELECTOR = "page";

    public GenericPageProcessor() {
    }


    @Override
    public void process(Page page) {
        try {
            List<CrawlerRequest> childRequestList = new LinkedList<>();
            String pageUrl = page.getRequest().getUrl();
            PageInfo pageInfo = pageParserRuleService.findOnePageInfoByRgx(pageUrl);
            Object context = null;
            String selectExpression = pageInfo.getPageValidationRule();
            if (StringUtils.isBlank(pageInfo.getPageValidationRule()) || DEFAULT_PAGE_SELECTOR.equals(pageInfo.getPageValidationRule()))
                context = page.getHtml();
            else {
                if (selectExpression.toLowerCase().contains("getjson()"))
                    context = SelectorExpressionResolver.resolve(null, page.getJson(), selectExpression);
                else
                    context = SelectorExpressionResolver.resolve(null, page.getHtml(), selectExpression);
            }
            if(context instanceof Selectable) {
                context = ((Selectable) context).get();
            }
            if(StringUtils.isEmpty(String.valueOf(context))) {
                if (pageParseListener != null)
                    pageParseListener.onError(page.getRequest(),"页面解析失败");
                //下载成功解析失败，那么该cookie无效
                Set<String> cookieIds = (Set<String>) page.getRequest().getExtra("cookieIds");
                if (cookieIds != null && cookieIds.size() > 0) {
                    //移除失效的cookie
                    pageSiteService.deleteCookieByIds(cookieIds);
                }
            } else {
                Set<PageParseRegion> regionList = getPageRegions(pageUrl);
                for (PageParseRegion pageParseRegion : regionList) {
                    Map<String, Object> result = parseOneRegion(page, pageParseRegion, childRequestList);
                    if (result != null) {
                        result.put("dataType", pageParseRegion.getDataType());
                        page.putField(pageParseRegion.getName(), result);
                    }
                }
                if (pageParseListener != null)
                    pageParseListener.onSuccess(page.getRequest(), childRequestList);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            if (pageParseListener != null)
                pageParseListener.onError(page.getRequest(),"页面解析失败");

            //下载成功解析失败，那么该cookie无效
            Set<String> cookieIds = (Set<String>) page.getRequest().getExtra("cookieIds");
            if (cookieIds != null && cookieIds.size() > 0) {
                //移除失效的cookie
                pageSiteService.deleteCookieByIds(cookieIds);
            }
        }
    }

    @SuppressWarnings("all")
    public Map<String, Object> parseOneRegion(Page page, PageParseRegion pageParseRegion, List<CrawlerRequest> childRequestList) {
        Selectable context = null;
        Request request = page.getRequest();
        String selectExpression = pageParseRegion.getSelectExpression();

        if (StringUtils.isBlank(selectExpression) || DEFAULT_PAGE_SELECTOR.equals(selectExpression))
            context = page.getHtml();
        else {
            if (selectExpression.toLowerCase().contains("getjson()"))
                context = SelectorExpressionResolver.resolve(request, page.getJson(), selectExpression);
            else
                context = SelectorExpressionResolver.resolve(request, page.getHtml(), selectExpression);
        }
        if (context == null) return null;

        Set<UrlParseRule> urlParseRules = pageParseRegion.getUrlParseRules();
        if (urlParseRules != null && urlParseRules.size() > 0) {
            childRequestList.addAll(parseUrlRules(context, request, urlParseRules));
        }

        Set<FieldParseRule> fieldParseRules = pageParseRegion.getFieldParseRules();
        if (fieldParseRules != null && fieldParseRules.size() > 0) {
            return parseFieldRules(context, request, fieldParseRules);
        }

        return null;
    }

    /**
     * 解析一个字段抽取规则
     *
     * @param context
     * @param request
     * @param fieldParseRuleList
     * @return
     */
    private Map<String, Object> parseFieldRules(Selectable context, Request request, Collection<FieldParseRule> fieldParseRuleList) {
        int i = 0;
        HashedMap resultMap = new HashedMap();
        List<Selectable> nodes = getNodes(context);

        for (Selectable node : nodes) {
            HashedMap childMap = new HashedMap();
            for (FieldParseRule fieldParseRule : fieldParseRuleList) {
                childMap.put(fieldParseRule.getFieldName(), SelectorExpressionResolver.resolve(request, node, fieldParseRule.getRule()));
            }
            resultMap.put(String.valueOf(i++), childMap);
        }
        if (nodes.size() > 1)
            return resultMap;
        else return (Map<String, Object>) resultMap.get("0");
    }


    /**
     * 解析一个Url抽取规则
     *
     * @param context
     * @param request
     * @param urlParseRuleList
     * @return
     */
    private List<CrawlerRequest> parseUrlRules(Selectable context, Request request, Collection<UrlParseRule> urlParseRuleList) {
        List<CrawlerRequest> childRequestList = new LinkedList<>();
        List<Selectable> nodes = getNodes(context);

        for (Selectable node : nodes) {
            if (node == null) continue;

            for (UrlParseRule urlParseRule : urlParseRuleList) {
                //解析url
                Object u = SelectorExpressionResolver.resolve(request, node, urlParseRule.getRule());
                //解析Url的参数
                Map<String, Object> urlParamMap = new HashMap<>();
                if (urlParseRule.getUrlRuleParams() != null)
                    for (UrlRuleParam ruleParam : urlParseRule.getUrlRuleParams()) {
                        urlParamMap.put(ruleParam.getParamName(), SelectorExpressionResolver.resolve(request, node, ruleParam.getExpression()));
                    }
                //组装成完整的URL
                if (u instanceof Collection) {
                    Collection<String> urlList = (Collection<String>) u;
                    if (urlList != null && urlList.size() > 0)
                        for (String url : urlList)
                            childRequestList.add(new CrawlerRequest(url, urlParseRule.getMethod(), urlParamMap));
                } else
                    childRequestList.add(new CrawlerRequest((String) u, urlParseRule.getMethod(), urlParamMap));
            }
        }
        return childRequestList;
    }


    private List<Selectable> getNodes(Selectable context) {
        List<Selectable> nodes = new LinkedList<>();

        if (context instanceof Json) {
//            List<Selectable> childs = ((Json) context).nodes();
//            for (Selectable child : childs) {
//                if(child instanceof PlainText)
//                nodes.add(new Json(((PlainText)child).getSourceTexts());
//            }
            nodes.add(context);
        } else nodes.addAll(context.nodes());
        return nodes;
    }


    @Override
    public Site getSite() {
        return Site.me();
    }

    public Set<PageParseRegion> getPageRegions(String pageUrl) {
        return pageParserRuleService.getPageRegions(pageUrl);
    }


}

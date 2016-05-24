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
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.dao.domain.FieldParseRule;
import yaycrawler.dao.domain.PageParseRegion;
import yaycrawler.dao.domain.UrlParseRule;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.listener.IPageCompletedListener;
import yaycrawler.spider.resolver.SelectorExpressionResolver;

import java.util.*;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Component
public class GenericPageProcessor implements PageProcessor {
    private static Logger logger = LoggerFactory.getLogger(GenericPageProcessor.class);
    @Autowired(required = false)
    private IPageCompletedListener completedListener;

    @Autowired
    private PageParserRuleService pageParseRuleService;
    private static String DEFAULT_PAGE_SELECTOR = "page";

    public GenericPageProcessor() {
    }


    @Override
    public void process(Page page) {
        try {
            List<CrawlerRequest> childRequestList = new LinkedList<>();
            String pageUrl = page.getRequest().getUrl();
            List<PageParseRegion> regionList = getPageRegionList(pageUrl);
            for (PageParseRegion pageParseRegion : regionList) {
                Map<String, Object> result = parseOneRegion(page, pageParseRegion, childRequestList);
                if (result != null) {
                    result.put("dataType", pageParseRegion.getDataType());
                    page.putField(pageParseRegion.getName(), result);
                }
            }
            if (completedListener != null)
                completedListener.onSuccess(page.getRequest(), childRequestList);
        } catch (Exception ex) {
            if (completedListener != null)
                completedListener.onError(page.getRequest());
            logger.error(ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("all")
    public Map<String, Object> parseOneRegion(Page page, PageParseRegion pageParseRegion, List<CrawlerRequest> childRequestList) {
        Selectable context = null;
        Request request = page.getRequest();
        String selectExpression = pageParseRegion.getSelectExpression();

        if (StringUtils.isBlank(selectExpression) || DEFAULT_PAGE_SELECTOR.equals(selectExpression))
            context = page.getHtml();
        else
            context = SelectorExpressionResolver.resolve(request, page.getHtml(), selectExpression);
        if (context == null) return null;

        List<UrlParseRule> urlParseRuleList = pageParseRegion.getUrlParseRules();
        if (urlParseRuleList != null && urlParseRuleList.size() > 0) {
            Set<String> childUrlSet = new HashSet<>();
            for (UrlParseRule urlParseRule : urlParseRuleList) {
                Object v = SelectorExpressionResolver.resolve(request,context, urlParseRule.getRule());
                if (v instanceof Collection) {
                    Collection<String> urlList = (Collection<String>) v;
                    if (urlList != null && urlList.size() > 0)
                        childUrlSet.addAll(urlList);
                } else childUrlSet.add(String.valueOf(v));
            }
//            page.addTargetRequests(new ArrayList<>(childSet));
            for (String childUrl : childUrlSet) {
                childRequestList.add(new CrawlerRequest(childUrl, UrlUtils.getDomain(childUrl), "GET"));
            }
        }

        List<FieldParseRule> fieldParseRuleList = pageParseRegion.getFieldParseRules();
        if (fieldParseRuleList != null && fieldParseRuleList.size() > 0) {
            int i = 0;
            HashedMap resultMap = new HashedMap();
            List<Selectable> nodes = new LinkedList<>();
            if (context instanceof Json)
                nodes.add(context);
            else nodes.addAll(context.nodes());

            for (Selectable node : nodes) {
                HashedMap childMap = new HashedMap();
                for (FieldParseRule fieldParseRule : fieldParseRuleList) {
                    childMap.put(fieldParseRule.getFieldName(), SelectorExpressionResolver.resolve(request,node, fieldParseRule.getRule()));
                }
                resultMap.put(String.valueOf(i++), childMap);
            }
            if (nodes.size() > 1)
                return resultMap;
            else return (Map<String, Object>) resultMap.get("0");
        }
        return null;
    }


    @Override
    public Site getSite() {
        return Site.me();
    }

    public List<PageParseRegion> getPageRegionList(String pageUrl) {
        return pageParseRuleService.getPageRegionList(pageUrl);
    }
}

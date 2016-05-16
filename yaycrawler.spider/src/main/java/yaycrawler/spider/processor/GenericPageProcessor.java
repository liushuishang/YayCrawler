package yaycrawler.spider.processor;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.dao.domain.FieldParseRule;
import yaycrawler.dao.domain.PageParseRegion;
import yaycrawler.dao.domain.UrlParseRule;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.resolver.SelectorExpressionResolver;

import java.util.*;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Component
public class GenericPageProcessor implements PageProcessor {
    @Autowired
    private PageParserRuleService pageParseRuleService;
    private static String DEFAULT_PAGE_SELECTOR = "page";

    public GenericPageProcessor() {
    }


    @Override
    public void process(Page page) {
        List<CrawlerRequest> childRequestList=new LinkedList<>();
        String pageUrl = page.getRequest().getUrl();
        List<PageParseRegion> regionList = getPageRegionList(pageUrl);
        for (PageParseRegion pageParseRegion : regionList) {
            Map<String, Object> result = parseOneRegion(page, pageParseRegion,childRequestList);
            if (result != null)
                page.putField(pageParseRegion.getName(), result);
        }
        //处理子级链接
        page.putField("childRequests", childRequestList);
    }

    @SuppressWarnings("all")
    public Map<String, Object> parseOneRegion(Page page, PageParseRegion pageParseRegion,List<CrawlerRequest> childRequestList) {
        Selectable context = null;
        if (DEFAULT_PAGE_SELECTOR.equals(pageParseRegion.getSelectExpression()))
            context = page.getHtml();
        else
            context = SelectorExpressionResolver.resolve(page.getHtml(), pageParseRegion.getSelectExpression());
        if (context == null) return null;
        List<UrlParseRule> urlParseRuleList = pageParseRegion.getUrlParseRules();
        if (urlParseRuleList != null && urlParseRuleList.size() > 0) {
            Set<String> childUrlSet = new HashSet<>();
            for (UrlParseRule urlParseRule : urlParseRuleList) {
                Collection<? extends String> urlList = SelectorExpressionResolver.resolve(context, urlParseRule.getRule());
                if (urlList != null && urlList.size() > 0)
                    childUrlSet.addAll(urlList);
            }
//            page.addTargetRequests(new ArrayList<>(childSet));
            for (String childUrl : childUrlSet) {
                childRequestList.add(new CrawlerRequest(childUrl, "GET", UrlUtils.getDomain(childUrl)));
            }
        }

        List<FieldParseRule> fieldParseRuleList = pageParseRegion.getFieldParseRules();
        if (fieldParseRuleList != null && fieldParseRuleList.size() > 0) {
            int i = 0;
            HashedMap resultMap = new HashedMap();
            List<Selectable> nodes = context.nodes();
            for (Selectable node : nodes) {
                HashedMap childMap = new HashedMap();
                for (FieldParseRule fieldParseRule : fieldParseRuleList) {
                    childMap.put(fieldParseRule.getFieldName(), SelectorExpressionResolver.resolve(node, fieldParseRule.getRule()));
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

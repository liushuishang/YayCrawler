package yaycrawler.spider.processor;

import org.apache.commons.collections.map.HashedMap;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import yaycrawler.common.domain.FieldParseRule;
import yaycrawler.common.domain.PageParseRegion;
import yaycrawler.common.domain.UrlParseRule;
import yaycrawler.common.resolver.SelectorExpressionResolver;
import yaycrawler.common.service.PageParserRuleService;

import java.util.*;

/**
 * Created by yuananyun on 2016/5/1.
 */
public class GenericPageProcessor implements PageProcessor {
    private PageParserRuleService pageParseRuleService;

    public GenericPageProcessor(PageParserRuleService pageParseRuleService) {
        this.pageParseRuleService = pageParseRuleService;
    }

    @Override
    public void process(Page page) {
        String pageUrl = page.getRequest().getUrl();
        List<PageParseRegion> regionList = pageParseRuleService.getPageRegionParseRule(pageUrl);
        for (PageParseRegion pageParseRegion : regionList) {
            Map<String, Object> result = parseOneRegion(page, pageParseRegion);
            if (result != null)
                page.putField(pageParseRegion.getName(), result);
        }
    }

    private Map<String, Object> parseOneRegion(Page page, PageParseRegion pageParseRegion) {
        Selectable content = page.getHtml().css(pageParseRegion.getCSSSelector());

        List<UrlParseRule> urlParseRuleList = pageParseRegion.getUrlParseRules();
        if (urlParseRuleList != null && urlParseRuleList.size() > 0) {
            Set<String> childSet = new HashSet<>();
            for (UrlParseRule urlParseRule : urlParseRuleList) {
                Collection<? extends String> urlList = SelectorExpressionResolver.resolve(content, urlParseRule.getRule());
                if (urlList != null && urlList.size() > 0)
                    childSet.addAll(urlList);
            }
            page.addTargetRequests(new ArrayList<>(childSet));
        }

        List<FieldParseRule> fieldParseRuleList = pageParseRegion.getFieldParseRules();
        if (fieldParseRuleList != null && fieldParseRuleList.size() > 0) {
            int i = 0;
            Map<String, Object> resultMap = new HashedMap();
            List<Selectable> nodes = content.nodes();
            for (Selectable node : nodes) {
                Map<String, Object> childMap = new HashedMap();
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
        return site;
    }


}

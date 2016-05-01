package yaycrawler.spider.processor;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by yuananyun on 2016/4/30.
 */
public class GenericPageProcessor implements PageProcessor {
    //url模式和页面处理器的对应关系
    private Map<String, PageProcessor> urlPatternAndProcessorMap;
    private Site site;

    public GenericPageProcessor(Site site, Map<String, PageProcessor> urlPatternAndProcessorMap) {
        this.urlPatternAndProcessorMap = urlPatternAndProcessorMap;
        this.site = site;
    }

    @Override
    public void process(Page page) {
        String url = page.getRequest().getUrl();
        for (Map.Entry<String, PageProcessor> pageProcessorEntry : urlPatternAndProcessorMap.entrySet()) {
            String key = pageProcessorEntry.getKey();
            if (StringUtils.isBlank(key)) continue;
            if (Pattern.matches(key, url)) {
                pageProcessorEntry.getValue().process(page);
                break;
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}

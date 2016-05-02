package yaycrawler.spider.crawler;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by yuananyun on 2016/5/2.
 */
public class YaySpider extends Spider {
    public YaySpider(Site site, PageProcessor pageProcessor) {
        super(pageProcessor);
        if(site!=null) {
            this.site = site;
            this.startRequests =site.getStartRequests();
        }
    }

    public static Spider create(Site site,PageProcessor pageProcessor) {
        return new YaySpider(site,pageProcessor);
    }
}

package yaycrawler.spider.crawler;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by yuananyun on 2016/5/2.
 */
public class YaySpider extends Spider {

    private String domain;

    public YaySpider(String domain,Site site, PageProcessor pageProcessor) {
        super(pageProcessor);
        if(site!=null) {
            this.site = site;
            this.startRequests =site.getStartRequests();
        }
        this.setDownloader(new HttpClientDownloader());
        //不需要把子连接加入到本地队列，因为我们的队列由Master统一管理
        spawnUrl=false;
        this.domain = domain;
        this.thread(3);
    }


    public String getDomain() {
        return domain;
    }
}

package yaycrawler.spider.service;

import yaycrawler.spider.crawler.YaySpider;

/**
 * Created by yuananyun on 2016/5/2.
 */
public  abstract  class BaseSpiderService {
    protected YaySpider spider;
    public abstract String   getDomain();
}

package yaycrawler.spider.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by yuananyun on 2016/4/30.
 */
public class DetailPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        //TODO 从数据库中获取页面字段解析规则，然后解析它


    }
    @Override
    public Site getSite() {
        return null;
    }
}

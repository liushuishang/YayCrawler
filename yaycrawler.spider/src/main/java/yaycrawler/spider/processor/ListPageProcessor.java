package yaycrawler.spider.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

/**
 * 用于处理列表页的解析，页面应该包括列表和分页控件
 * Created by yuananyun on 2016/4/30.
 */
public class ListPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        Html content = page.getHtml();
        //TODO 从数据库中获取抓取规则
//        目前只抓取列表中的主链接
        List<String> itemUrls = content.xpath("div[@class='list-con']").links().regex(".*info.php?.*").all();
        page.addTargetRequests(itemUrls);
        //列表页
        List<String> pageUrls = content.xpath("ul[@class='pageno']").links().all();
        page.addTargetRequests(pageUrls);
    }

    @Override
    public Site getSite() {
        return null;
    }
}

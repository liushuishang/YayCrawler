package yaycrawler.spider.persistent;

import java.util.Map;

/**
 * 爬虫结果持久化工具
 * Created by ucs_yuananyun on 2016/5/11.
 */
public interface IResultPersistentService {

    /**
     * 保存某个页面的爬取结果
     *
     * @param pageUrl 页面的URL
     * @param data    爬取的结果数据
     * @return 是否保存成功
     */
    boolean saveCrawlerResult(String pageUrl, Map<String, Object> data);

}

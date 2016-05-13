package yaycrawler.common.model;

import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class CrawlerResult {

    private String key;

    private List<CrawlerRequest> crawlerRequestList;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<CrawlerRequest> getCrawlerRequestList() {
        return crawlerRequestList;
    }

    public void setCrawlerRequestList(List<CrawlerRequest> crawlerRequestList) {
        this.crawlerRequestList = crawlerRequestList;
    }
}

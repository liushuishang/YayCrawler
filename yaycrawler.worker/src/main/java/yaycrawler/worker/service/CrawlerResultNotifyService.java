package yaycrawler.worker.service;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.worker.communication.MasterActor;

import java.util.List;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/16.
 */
@Service
public class CrawlerResultNotifyService implements IResultPersistentService {

    @Autowired
    private MasterActor masterActor;

    @Override
    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> data) {
        CrawlerResult crawlerResult = new CrawlerResult();
        String key = MapUtils.getString(data, "_id");
        crawlerResult.setKey(key);
        crawlerResult.setSuccess(true);
        crawlerResult.setCrawlerRequestList((List<CrawlerRequest>) data.getOrDefault("childRequests", null));

       return masterActor.notifyCrawlerResult(crawlerResult);

    }
}

package yaycrawler.worker.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentDataType;

import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/11.
 */
@Service
public class MongDBPersistentService implements IResultPersistentService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> data) {
        String _id = DigestUtils.shaHex(pageUrl);
        data.put("pageUrl", pageUrl);
        data.put("_id", _id);
        String collectionName = UrlUtils.getDomain(pageUrl).replace(".", "_");
        mongoTemplate.save(data, collectionName);
        return true;
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.MAP;
    }
}

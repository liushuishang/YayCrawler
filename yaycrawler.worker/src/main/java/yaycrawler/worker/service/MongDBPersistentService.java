package yaycrawler.worker.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(MongDBPersistentService.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override

    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> data) {
        try {
            String _id = DigestUtils.sha1Hex(pageUrl);
            data.put("pageUrl", pageUrl);
            data.put("_id", _id);
            data.put("timestamp", System.currentTimeMillis());
            String collectionName = UrlUtils.getDomain(pageUrl).replace(".", "_");
            mongoTemplate.save(data, collectionName);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.MAP;
    }
}

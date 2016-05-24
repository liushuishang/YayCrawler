package yaycrawler.admin.service;

import org.springframework.stereotype.Component;
import yaycrawler.spider.persistent.IResultPersistentService;

import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Component
public class FileResultPersistentService implements IResultPersistentService {

    @Override
    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> data) {
        return false;
    }

    @Override
    public String getSupportedDataType() {
        return null;
    }
}

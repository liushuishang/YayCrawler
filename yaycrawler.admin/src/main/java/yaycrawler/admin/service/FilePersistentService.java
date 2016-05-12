package yaycrawler.admin.service;

import org.springframework.stereotype.Service;
import yaycrawler.spider.persistent.IResultPersistentService;

/**
 * Created by ucs_yuananyun on 2016/5/12.
 */
@Service
public class FilePersistentService implements IResultPersistentService {
    @Override
    public boolean saveCrawlerResult(String pageUrl, Object data) {
        return false;
    }
}

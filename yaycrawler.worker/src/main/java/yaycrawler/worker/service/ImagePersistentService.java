package yaycrawler.worker.service;

import org.springframework.stereotype.Component;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentDataType;

import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/23.
 */
@Component
public class ImagePersistentService implements IResultPersistentService {



    @Override
    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> data) {
        //TODO 下载图片
        return false;
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.IMAGE;
    }

}

package yaycrawler.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**爬虫结果查询服务类
 * Created by ucs_yuananyun on 2016/5/30.
 */
@Service
public class CrawlerResultRetrivalService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Object  RetrivalByTaskId(String collectionName,String taskId)
    {
        return mongoTemplate.findById(taskId, LinkedHashMap.class, collectionName);

    }

}

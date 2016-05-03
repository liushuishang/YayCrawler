package yaycrawler.spider.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Component
public class MongoDbPipeline implements Pipeline {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoDbPipeline(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String domain=task.getSite().getDomain();

        //创建以domain命名的数据库
        mongoTemplate.getDb().command("user " + domain);

    }
}

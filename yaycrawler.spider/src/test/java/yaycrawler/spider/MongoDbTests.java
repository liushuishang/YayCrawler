package yaycrawler.spider;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yuananyun on 2016/5/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class MongoDbTests {
    @Autowired
    private  MongoTemplate mongoTemplate;

    @Test
    public void TestMongoTemplate()
    {
        DB db = mongoTemplate.getDb();
        db.createCollection("0731",new BasicDBObject(100));



    }
}

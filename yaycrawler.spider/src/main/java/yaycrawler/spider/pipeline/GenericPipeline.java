package yaycrawler.spider.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import yaycrawler.spider.persistent.IResultPersistentService;

import java.util.List;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/11.
 */
@Component
public class GenericPipeline implements Pipeline {
    @Autowired
    private List<IResultPersistentService> persistentServiceList;

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (IResultPersistentService persistentService : persistentServiceList) {
            try {
                String pageUrl = resultItems.getRequest().getUrl();
                Map<String, Object> dataMap = resultItems.getAll();

                if(dataMap.size()==0) continue;

                dataMap.put("pageUrl", pageUrl);
                persistentService.saveCrawlerResult(pageUrl, dataMap);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

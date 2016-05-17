package yaycrawler.master.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.master.communication.WorkerActor;
import yaycrawler.master.service.CrawlerQueueService;

/**
 * 全局任务调度器
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Component
public class CrawlerTaskDispatcher {

    @Autowired
    private CrawlerQueueService queueService;

    @Autowired
    private WorkerActor workerActor;

    public void dealResultNotify(CrawlerResult crawlerResult) {
        if(crawlerResult.isSuccess()) {
            //TODO 把结果加入队列中
//        workerActor.assignTasks(crawlerResult.getCrawlerRequestList());
//            workerActor.regeditTasks(crawlerResult.getCrawlerRequestList());
//            workerActor.removeTask(crawlerResult.getKey());
        }
        else{
            //TODO 执行失败的处理

        }
    }

    /**
     * 接收任务
     */

    /**
     * 分派任务
     */


    /**
     * 任务成功
     */

    /**
     * 任务失败
     */

}

package yaycrawler.master.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.model.WorkerHeartbeat;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.master.communication.WorkerActor;
import yaycrawler.master.model.MasterContext;
import yaycrawler.master.service.CrawlerQueueService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局任务调度器
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Component
public class CrawlerTaskDispatcher {

    @Value("${worker.task.batchSize}")
    private Integer batchSize;


    @Autowired
    private CrawlerQueueService queueService;

    @Autowired
    private WorkerActor workerActor;

    public void dealResultNotify(CrawlerResult crawlerResult) {
        if (crawlerResult.isSuccess()) {
            //TODO 把结果加入队列中
            queueService.regeditQueues(crawlerResult.getCrawlerRequestList(),false);
            queueService.removeCrawler(crawlerResult.getKey());
        } else {
            //TODO 执行失败的处理
            queueService.moveFailQueue(crawlerResult.getKey(),crawlerResult.getMessage());
        }
    }

    /**
     * 接收任务
     */

    /**
     * 分派任务
     */
    public void assingTask(WorkerHeartbeat workerHeartbeat) {
        ConcurrentHashMap<String, WorkerRegistration> workerListMap = MasterContext.workerRegistrationMap;
        WorkerRegistration workerRegistration = workerListMap.get(workerHeartbeat.getWorkerId());
        if(workerRegistration==null) return;

        int leftCount = batchSize - workerHeartbeat.getWaitTaskCount();
        if(leftCount <= 0)
            return;
        List<CrawlerRequest> crawlerRequests = queueService.listQueues(leftCount);
        if(crawlerRequests.size() == 0)
            return;
        boolean flag = workerActor.assignTasks(workerRegistration, crawlerRequests);
        if (flag) {
            queueService.moveRunningQueue(workerHeartbeat,crawlerRequests);
        }
    }


}

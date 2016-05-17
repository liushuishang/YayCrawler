package yaycrawler.master.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    @Autowired
    private Environment environment;

    @Autowired
    private CrawlerQueueService queueService;

    @Autowired
    private WorkerActor workerActor;

    public void dealResultNotify(CrawlerResult crawlerResult) {
        if (crawlerResult.isSuccess()) {
            //TODO 把结果加入队列中
            queueService.regeditQueues(crawlerResult.getCrawlerRequestList());
            queueService.removeCrawler(crawlerResult.getKey());
        } else {
            //TODO 执行失败的处理
            queueService.moveFailQueue(crawlerResult.getKey());
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
        int count = Integer.parseInt(environment.getProperty("work.task.count"));
        List<CrawlerRequest> crawlerRequests = queueService.listQueues(count - workerHeartbeat.getWaitTaskCount());
        boolean flag = workerActor.assignTasks(workerRegistration, crawlerRequests);
        if (flag) {
            queueService.moveRunningQueue(crawlerRequests);
        }
    }

}

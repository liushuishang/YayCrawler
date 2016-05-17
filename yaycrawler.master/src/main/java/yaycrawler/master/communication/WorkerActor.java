package yaycrawler.master.communication;

import org.springframework.stereotype.Component;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.WorkerRegistration;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
public class WorkerActor {

//    @Autowired
//    private CrawlerQueueService crawlerQueueService;

    public boolean assignTasks(WorkerRegistration workerRegistration,List<CrawlerRequest> taskList) {
//        ConcurrentHashMap<String, WorkerRegistration> workerListMap = MasterContext.workerRegistrationMap;
//        for (WorkerRegistration workerRegistration : workerListMap.values()) {
//            List<CrawlerRequest> crawlerRequestList = crawlerQueueService.listWorks(10);
//            String targetUrl = CommunicationAPIs.getFullRemoteUrl(workerRegistration.getWorkerContextPath(), CommunicationAPIs.MASTER_POST_WORKER_TASK_ASSIGN);
//            RestFulResult result = HttpUtils.doHttpExecute(targetUrl, HttpMethod.POST, crawlerRequestList);
//
//            return result != null && !result.hasError();
////            if (result==null||result.hasError()) {
////
////            } else {
////                workInfoService.moveRunningQueue(crawlerRequestList);
////            }
////        }

        return false;

    }

//    public void regeditTasks(List<CrawlerRequest> crawlerRequestList) {
//        crawlerQueueService.regeditWorks(crawlerRequestList);
//    }
//
//    public void removeTask(String key ) {
//        crawlerQueueService.removeCrawler(key);
//    }

}

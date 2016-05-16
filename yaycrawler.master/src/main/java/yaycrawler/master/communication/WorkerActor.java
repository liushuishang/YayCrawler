package yaycrawler.master.communication;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CommunicationAPIs;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.common.utils.HttpUtils;
import yaycrawler.master.model.MasterContext;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
public class WorkerActor {

    public void assignTasks(List<CrawlerRequest> crawlerRequestList) {
        ConcurrentHashMap<String, WorkerRegistration> workerListMap = MasterContext.workerRegistrationMap;
        for (WorkerRegistration workerRegistration : workerListMap.values()) {
            String targetUrl = CommunicationAPIs.getFullRemoteUrl(workerRegistration.getWorkerContextPath(), CommunicationAPIs.MASTER_POST_WORKER_TASK_ASSIGN);
            RestFulResult result = HttpUtils.doHttpExecute(targetUrl, HttpMethod.POST, crawlerRequestList);
            if (result==null||result.hasError()) {

            } else {

            }
        }

    }

}

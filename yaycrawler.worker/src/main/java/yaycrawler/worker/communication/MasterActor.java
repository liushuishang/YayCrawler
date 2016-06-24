package yaycrawler.worker.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.*;
import yaycrawler.common.utils.HttpUtils;
import yaycrawler.worker.exception.WorkerResultNotifyFailureException;
import yaycrawler.worker.model.WorkerContext;
import yaycrawler.worker.service.TaskScheduleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
public class MasterActor {
    private static Logger logger = LoggerFactory.getLogger(MasterActor.class);

    @Autowired
    private TaskScheduleService taskScheduleService;

    /**
     * Worker注册Master
     *
     * @return
     */
    public boolean register() {
        logger.info("worker-{}开始向Master申请注册", WorkerContext.getWorkerId());
        WorkerRegistration workerRegistration = new WorkerRegistration(WorkerContext.getWorkerId(), WorkerContext.getContextPath());
        workerRegistration.setHeartbeatInteval(WorkerContext.getHeartbeatInteval());

        String targetUrl = CommunicationAPIs.getFullRemoteUrl(WorkerContext.getMasterServerAddress(), CommunicationAPIs.WORKER_POST_MASTER_REGISTER);
        RestFulResult result = HttpUtils.doSignedHttpExecute(WorkerContext.getSignatureSecret(), targetUrl, HttpMethod.POST, workerRegistration);
        if (result.hasError()) {
            logger.error("worker-{}注册Master失败,原因:{}", WorkerContext.getWorkerId(),result.getMessage());
//            throw new WorkerRegisteFailureException(result.getMessage());
            return false;
        }
        logger.info("worker-{}向Master注册成功！", WorkerContext.getWorkerId());
        return true;
    }

    public boolean sendHeartbeart() {
//        if (!WorkerContext.isSuccessRegisted) return false;
        logger.debug("worker-{}开始向Master发送心跳", WorkerContext.getWorkerId());
        WorkerHeartbeat heartbeat = new WorkerHeartbeat(WorkerContext.getWorkerId());
        heartbeat.setWorkerContextPath(WorkerContext.getContextPath());
        heartbeat.setHeartbeatInteval(WorkerContext.getHeartbeatInteval());

        //收集已经完成的任务，然后汇报给Master
        Set<Map.Entry<String, CrawlerResult>> completedResultSet = WorkerContext.completedResultMap.entrySet();
        List<CrawlerResult> completedCrawlerResultList = new ArrayList<>(completedResultSet.size());
        completedCrawlerResultList.addAll(completedResultSet.stream().map(Map.Entry<String, CrawlerResult>::getValue).collect(Collectors.toList()));
        heartbeat.setCompletedCrawlerResultList(completedCrawlerResultList);

        String targetUrl = CommunicationAPIs.getFullRemoteUrl(WorkerContext.getMasterServerAddress(), CommunicationAPIs.WORKER_POST_MASTER_HEARTBEAT);
        heartbeat.setWaitTaskCount(taskScheduleService.getRunningTaskCount());
        RestFulResult result = HttpUtils.doSignedHttpExecute(WorkerContext.getSignatureSecret(), targetUrl, HttpMethod.POST, heartbeat);

        if (result.hasError()) {
            //心跳失败的处理
            WorkerContext.heartbeatFailCount++;
            if(WorkerContext.heartbeatFailCount>=3){
                //worker应该停止自身所有的任务
                logger.info("Worker已经与Master失联超过3个心跳周期，现在停止自身所有的任务");
                taskScheduleService.interruptAllTasks();
                WorkerContext.heartbeatFailCount = 0;
            }
        }
        else{
            //移除已经汇报成功的任务
            for (Map.Entry<String, CrawlerResult> resultEntry : completedResultSet) {
                WorkerContext.completedResultMap.remove(resultEntry.getKey());
            }
        }
        return true;
    }


    /**
     * Worker通知Master爬取结果成功
     *
     * @param crawlerResult
     * @return
     */
    public boolean notifyTaskSuccess(CrawlerResult crawlerResult) {
        logger.debug("任务{}执行成功，现在通知Master……", crawlerResult.getKey());
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(WorkerContext.getMasterServerAddress(), CommunicationAPIs.WORKER_POST_MASTER_SUCCESS_NOTIFY);
        RestFulResult result = HttpUtils.doSignedHttpExecute(WorkerContext.getSignatureSecret(), targetUrl, HttpMethod.POST, crawlerResult);
        if (result.hasError()) {
            logger.error("任务{}执行成功，通知Master失败！", crawlerResult.getKey());
            throw new WorkerResultNotifyFailureException(result.getMessage());
        }
        return true;
    }


    public boolean notifyTaskFailure(CrawlerResult crawlerResult) {
        logger.debug("任务{}执行失败，现在通知Master……", crawlerResult.getKey());
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(WorkerContext.getMasterServerAddress(), CommunicationAPIs.WORKER_POST_MASTER_FAILURE_NOTIFY);
        RestFulResult result = HttpUtils.doSignedHttpExecute(WorkerContext.getSignatureSecret(), targetUrl, HttpMethod.POST, crawlerResult);
        if (result.hasError()) {
            logger.error("任务{}执行失败，通知Master失败！", crawlerResult.getKey());
            throw new WorkerResultNotifyFailureException(result.getMessage());
        }
        return true;
    }
}

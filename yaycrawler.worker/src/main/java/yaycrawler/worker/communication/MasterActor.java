package yaycrawler.worker.communication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CommunicationAPIs;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.common.utils.HttpUtils;
import yaycrawler.worker.exception.WorkerRegisteFailureException;
import yaycrawler.worker.exception.WorkerResultNotifyFailureException;
import yaycrawler.worker.model.WorkerContext;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
public class MasterActor {
    @Value("${master.server.address}")
    private String masterServerAddress;

    @Value("${context.path}")
    private String contextPath;

    /**
     * Worker注册Master
     *
     * @return
     */
    public boolean register() {
        WorkerRegistration workerRegistration = new WorkerRegistration(WorkerContext.workerId, contextPath);
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.WORKER_POST_MASTER_REGISTER);
        RestFulResult result = HttpUtils.doHttpExecute(targetUrl, HttpMethod.POST, workerRegistration);
        if (result.hasError())
            throw new WorkerRegisteFailureException(result.getMessage());
        return true;
    }

    /**
     * Worker通知Master爬取结果成功
     *
     * @param crawlerResult
     * @return
     */
    public boolean notifyTaskSuccess(CrawlerResult crawlerResult) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.WORKER_POST_MASTER_SUCCESS_NOTIFY);
        RestFulResult result = HttpUtils.doHttpExecute(targetUrl, HttpMethod.POST, crawlerResult);
        if (result.hasError())
            throw new WorkerResultNotifyFailureException(result.getMessage());
        return true;
    }


    public boolean notifyTaskFailure(CrawlerResult crawlerResult) {
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.WORKER_POST_MASTER_FAILURE_NOTIFY);
        RestFulResult result = HttpUtils.doHttpExecute(targetUrl, HttpMethod.POST, crawlerResult);
        if (result.hasError())
            throw new WorkerResultNotifyFailureException(result.getMessage());
        return true;
    }
}

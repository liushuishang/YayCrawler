package yaycrawler.worker.communication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CommunicationAPIs;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.common.utils.HttpUtils;
import yaycrawler.worker.exception.WorkerRegisteFailureException;
import yaycrawler.worker.model.WorkerContext;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Component
public class MasterActor {
    @Value("${master.server.address}")
    private String masterServerAddress;

    public boolean register() {
        WorkerRegistration workerRegistration = new WorkerRegistration(WorkerContext.workerId,WorkerContext.getContextPath());
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(masterServerAddress, CommunicationAPIs.WORKER_POST_MASTER_REGISTER);
        RestFulResult result = HttpUtils.postForResult(targetUrl, workerRegistration);
        if (result.hasError())
            throw new WorkerRegisteFailureException(result.getMessage());

        return !result.hasError();
    }

}

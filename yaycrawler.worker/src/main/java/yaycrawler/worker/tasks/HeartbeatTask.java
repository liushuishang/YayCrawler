package yaycrawler.worker.tasks;

import yaycrawler.common.model.CommunicationAPIs;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.model.WorkerHeartbeat;
import yaycrawler.common.utils.HttpUtils;
import yaycrawler.worker.exception.WorkerHeartbeatFailureException;
import yaycrawler.worker.model.WorkerContext;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class HeartbeatTask {
    public HeartbeatTask() {

    }

    private int tryCount = 3;

    public void sendHeartbeart() {
        if (!WorkerContext.isSuccessRegisted) return;

        WorkerHeartbeat heartbeat = new WorkerHeartbeat();
        heartbeat.setWorkerId(WorkerContext.workerId);
        heartbeat.setLastTime(System.currentTimeMillis());

        int count = tryCount;
        String targetUrl = CommunicationAPIs.getFullRemoteUrl(WorkerContext.masterServerAddress, CommunicationAPIs.WORKER_POST_MASTER_HEARTBEAT);
        while (count > 0) {
            RestFulResult result = HttpUtils.postForResult(targetUrl, heartbeat);
            if (!result.hasError())
                break;
            count--;
        }
        if (count < 0)
            throw new WorkerHeartbeatFailureException();
    }
}

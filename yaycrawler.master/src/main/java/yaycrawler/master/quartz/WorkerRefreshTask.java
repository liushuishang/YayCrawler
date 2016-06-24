package yaycrawler.master.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.master.model.MasterContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ucs_yuananyun on 2016/5/31.
 */
public class WorkerRefreshTask {
    private static final Logger logger = LoggerFactory.getLogger(WorkerRefreshTask.class);

    public void refreshWorker() {
        logger.info("开始刷新已注册的worker……");
        //移除已经超时的Worker
        ConcurrentHashMap<String, WorkerRegistration> workerRegistrationMap = MasterContext.workerRegistrationMap;
        long currentTime = System.currentTimeMillis();

        for (WorkerRegistration registration : workerRegistrationMap.values()) {
            if(registration==null|| registration.getHeartbeatInteval()==null) continue;
            Long lastTime = registration.getLastHeartbeatTime();
            if(lastTime==null) lastTime = System.currentTimeMillis();
            if (currentTime - lastTime >=2 * registration.getHeartbeatInteval()) {
                logger.info("{}心跳已经超时，Master移除该Worker！", registration.toString());
                workerRegistrationMap.remove(registration.getWorkerContextPath());
            }
        }
    }
}

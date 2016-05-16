package yaycrawler.master.model;

import yaycrawler.common.model.WorkerHeartbeat;
import yaycrawler.common.model.WorkerRegistration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Master的环境上下文
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class MasterContext {
    public  static ConcurrentHashMap<String, WorkerRegistration> workerRegistrationMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, WorkerHeartbeat> workerHeartbeatMap = new ConcurrentHashMap<>();

    public static void registeWorker(WorkerRegistration registration) {
        if (registration.getWorkerId() != null)
            workerRegistrationMap.put(registration.getWorkerId(), registration);
    }


    public static void receiveWorkerHeartbeat(WorkerHeartbeat heartbeat) {
        if (workerRegistrationMap.containsKey(heartbeat.getWorkerId()))
            workerHeartbeatMap.put(heartbeat.getWorkerId(), heartbeat);
        else throw new RuntimeException(String.format("worker %s 未注册，但是却发送了心跳！", heartbeat.getWorkerId()));
    }


}

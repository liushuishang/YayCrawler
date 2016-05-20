package yaycrawler.master.model;

import yaycrawler.common.model.WorkerHeartbeat;
import yaycrawler.common.model.WorkerRegistration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Master的环境上下文
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class MasterContext {

    public static ConcurrentHashMap<String, WorkerRegistration> workerRegistrationMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, WorkerHeartbeat> workerHeartbeatMap = new ConcurrentHashMap<>();

    public static void registeWorker(WorkerRegistration registration) {
        if (registration.getWorkerId() != null)
            workerRegistrationMap.put(registration.getWorkerId(), registration);
    }


    public static void receiveWorkerHeartbeat(WorkerHeartbeat heartbeat) {
        long currentTime = System.currentTimeMillis();
        if (workerRegistrationMap.containsKey(heartbeat.getWorkerId())) {
            heartbeat.setLastTime(currentTime);
            workerHeartbeatMap.put(heartbeat.getWorkerId(), heartbeat);
        }

        //移除已经超时的Worker
        for (WorkerRegistration registration : workerRegistrationMap.values()) {
            Long lastTime = workerHeartbeatMap.get(registration.getWorkerId()).getLastTime();
            if (currentTime - lastTime >= 2 * registration.getHeartbeatInteval()) {
                workerRegistrationMap.remove(registration.getWorkerId());
            }
        }
    }


}

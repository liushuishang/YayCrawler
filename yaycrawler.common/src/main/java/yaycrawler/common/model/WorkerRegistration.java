package yaycrawler.common.model;

/**
 * Created by ucs_yuananyun on 2016/5/12.
 */
public class WorkerRegistration {

    private  String workerId;
    private String workerContextPath;
    private Long heartbeatInteval;
    private int waitTaskCount;
    /**
     * 最后的心跳时间
     */
    private Long lastHeartbeatTime=0L;

    public WorkerRegistration() {
    }

    public WorkerRegistration(String workerId,String workerContextPath) {
        this.workerId = workerId;
        this.workerContextPath = workerContextPath;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerContextPath() {
        return workerContextPath;
    }

    public void setWorkerContextPath(String workerContextPath) {
        this.workerContextPath = workerContextPath;
    }

    public Long getHeartbeatInteval() {
        return heartbeatInteval;
    }

    public void setHeartbeatInteval(Long heartbeatInteval) {
        this.heartbeatInteval = heartbeatInteval;
    }

    @Override
    public String toString() {
        return "WorkerRegistration{" +
                "workerId='" + workerId + '\'' +
                ", workerContextPath='" + workerContextPath + '\'' +
                ", heartbeatInteval=" + heartbeatInteval +
                '}';
    }

    public Long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(Long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public int getWaitTaskCount() {
        return waitTaskCount;
    }

    public void setWaitTaskCount(int waitTaskCount) {
        this.waitTaskCount = waitTaskCount;
    }
}

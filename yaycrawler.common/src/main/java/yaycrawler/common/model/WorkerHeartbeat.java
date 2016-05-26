package yaycrawler.common.model;

/**
 * Created by ucs_yuananyun on 2016/5/12.
 */
public class WorkerHeartbeat {
    private String workerId;
    private int waitTaskCount;


    public WorkerHeartbeat(String workerId) {
        this.workerId = workerId;
    }

    public WorkerHeartbeat() {

    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public int getWaitTaskCount() {
        return waitTaskCount;
    }

    public void setWaitTaskCount(int waitTaskCount) {
        this.waitTaskCount = waitTaskCount;
    }

}

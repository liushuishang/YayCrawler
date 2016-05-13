package yaycrawler.worker.exception;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class WorkerHeartbeatFailureException extends   RuntimeException  {
    @Override
    public String getMessage() {
        return "Worker向Master发送心跳失败！";
    }
}

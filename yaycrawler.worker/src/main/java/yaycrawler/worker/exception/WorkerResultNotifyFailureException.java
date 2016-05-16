package yaycrawler.worker.exception;

/**
 * Created by ucs_yuananyun on 2016/5/16.
 */
public class WorkerResultNotifyFailureException extends   RuntimeException  {
    public WorkerResultNotifyFailureException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Worker通知Master结果时出现异常！";
    }
}

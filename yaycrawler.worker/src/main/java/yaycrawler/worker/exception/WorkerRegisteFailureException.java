package yaycrawler.worker.exception;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
public class WorkerRegisteFailureException extends   RuntimeException {

    public WorkerRegisteFailureException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Worker注册Master失败！";
    }
}

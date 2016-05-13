package yaycrawler.common.model;

/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
public class RestFulResult {
    private int status;
    private String message;
    private Object data;

    public RestFulResult(int status, String message, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public RestFulResult() {
    }

    public boolean hasError()
    {
        return status == 0;
    }

    public static RestFulResult success(Object data) {
        return new RestFulResult(1, null, data);
    }

    public static RestFulResult failure(String errorMsg) {
        return new RestFulResult(0, errorMsg, null);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

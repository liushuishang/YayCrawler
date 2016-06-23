package yaycrawler.monitor.login;

import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/6/23.
 */
public class LoginResult {
    private boolean isSuccess;
    private Map<String, Object> cookies;

    public LoginResult() {
    }

    public LoginResult(boolean isSuccess, Map<String, Object> cookies) {
        this.isSuccess = isSuccess;
        this.cookies = cookies;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Map<String, Object> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, Object> cookies) {
        this.cookies = cookies;
    }
}

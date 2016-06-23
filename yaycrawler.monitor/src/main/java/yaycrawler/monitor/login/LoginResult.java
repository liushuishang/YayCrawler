package yaycrawler.monitor.login;

import yaycrawler.common.model.PhantomCookie;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/6/23.
 */
public class LoginResult {
    private boolean isSuccess;
    private List<PhantomCookie> cookies;

    public LoginResult() {
    }

    public LoginResult(boolean isSuccess, List<PhantomCookie> cookies) {
        this.isSuccess = isSuccess;
        this.cookies = cookies;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<PhantomCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<PhantomCookie> cookies) {
        this.cookies = cookies;
    }
}

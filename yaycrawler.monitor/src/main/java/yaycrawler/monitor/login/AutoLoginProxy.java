package yaycrawler.monitor.login;

import org.springframework.stereotype.Component;

/**
 * Created by ucs_yuananyun on 2016/6/23.
 */
@Component
public class AutoLoginProxy {
    public LoginResult login(String pageUrl, String loginJsFileName, String rawText) {

       LoginResult loginResult=new LoginResult();
        return loginResult;
    }
}

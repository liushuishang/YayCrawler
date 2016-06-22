package yaycrawler.monitor.captcha;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yaycrawler.monitor.captcha.geetest.GeetestCaptchaIdentification;

/**
 * 验证码识别代理类
 * Created by ucs_yuananyun on 2016/6/22.
 */
@Component
public class CaptchaIdentificationProxy {

    @Value("${server.address}")
    private String serverIP;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.context-path}")
    private String serverContextPath;



    public boolean recognition(String pageUrl, String pageContent) {
        if (StringUtils.isBlank(pageContent)) return false;
        if (pageContent.contains("http://api.geetest.com/get.php")) {
            String resolverAddress = String.format("http://%s:%s%s/%s", serverIP, serverPort, serverContextPath, "resolveGeetestSlicePosition");
            return GeetestCaptchaIdentification.process(pageUrl, resolverAddress);
        }
        return false;
    }
}

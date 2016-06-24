package yaycrawler.monitor.captcha;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.monitor.captcha.geetest.GeetestCaptchaIdentification;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 验证码识别代理类
 * Created by ucs_yuananyun on 2016/6/22.
 */
@Component
public class CaptchaIdentificationProxy {
    private static Logger logger = LoggerFactory.getLogger(CaptchaIdentificationProxy.class);

    @Value("${server.address}")
    private String serverIP;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.context-path}")
    private String serverContextPath;



    public boolean recognition(String pageUrl,String cookies,String jsFileName, String pageContent) {
        if(StringUtils.isBlank(jsFileName))
        {
            logger.error("jsFileName不能为空！");
            return false;
        }
        if (StringUtils.isBlank(pageContent)) return false;
        if (pageContent.contains("http://api.geetest.com/get.php")) {
            String resolverAddress = String.format("http://%s:%s%s/%s", serverIP, serverPort, serverContextPath, "resolveGeetestSlicePosition");

            String domain = UrlUtils.getDomain(pageUrl);
            String cookieValue= null;
            try {
                cookieValue = URLEncoder.encode(cookies.replaceAll(" ", "%20"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            return GeetestCaptchaIdentification.process(pageUrl,domain,cookieValue,jsFileName, resolverAddress);
        }
        return false;
    }
}

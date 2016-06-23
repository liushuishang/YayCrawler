package yaycrawler.monitor.captcha.geetest;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.common.utils.CasperjsProgramManager;

/**
 * 极验验证码识别
 * Created by ucs_yuananyun on 2016/6/14.
 */
public class GeetestCaptchaIdentification {
    private static Logger logger = LoggerFactory.getLogger(GeetestCaptchaIdentification.class);

    /**
     * 验证码自动识别     *
     *
     * @param pageUrl             包含验证码的页面url
     * @param deltaResolveAddress 能够解析验证码移动位移的服务地址
     * @return
     */
    public static boolean process(String pageUrl, String deltaResolveAddress) {
        logger.info("滑块位置服务：" + deltaResolveAddress);
        if (pageUrl == null) return false;

        String result = CasperjsProgramManager.launch("geetest.js", pageUrl, deltaResolveAddress, " --web-security=no", "--ignore-ssl-errors=true");
        logger.info("验证码识别结果：" + result);
        if (result != null && result.contains("验证通过"))
            return true;
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        int totalCount = 1;
        int successCount = 0;
        int retryCount = 0;
        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < totalCount; i++) {
            stopWatch.reset();
            stopWatch.start();
            if (startIdentification())
                successCount++;
            else {
                int t = retryCount;
                while (t > 0) {
                    System.out.println("重试一次");
                    if (startIdentification()) {
                        successCount++;
                        break;
                    }
                    t--;
                }
            }
            stopWatch.stop();
            System.out.println("本次调用耗时：(毫秒)" + stopWatch.getTime());
        }
        System.out.println("调用" + totalCount + "次，失败重试" + retryCount + "次的情况下，共成功" + successCount + "次");
    }


    private static boolean startIdentification() {
//  return GeetestCaptchaIdentification.process("http://user.geetest.com/login?url=http:%2F%2Faccount.geetest.com%2Freport","http://localhost:8086/worker/resolveGeetestSlicePosition");
        return GeetestCaptchaIdentification.process("http://www.qichacha.com/search_index?index=0&p=1&key=%E5%BD%A6%E4%B8%9C%E5%A1%91%E8%83%B6%E5%85%AC%E5%8F%B8", "http://localhost:8086/worker/resolveGeetestSlicePosition");
    }
}

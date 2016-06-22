package yaycrawler.monitor.captcha.geetest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * 极验验证码识别
 * Created by ucs_yuananyun on 2016/6/14.
 */
public class GeetestCaptchaIdentification {
    private static Logger logger = LoggerFactory.getLogger(GeetestCaptchaIdentification.class);


    /**
     * 验证码自动识别     *
     * @param pageUrl 包含验证码的页面url
     * @param deltaResolveAddress 能够解析验证码移动位移的服务地址
     * @return
     */
    public static boolean process(String pageUrl,String deltaResolveAddress) {
        if (pageUrl == null) return false;
        String path =GeetestCaptchaIdentification.class.getResource("/").getPath();
        path = path.substring(1, path.lastIndexOf("/") + 1);
        String progam = "";
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        String phantomJsPath = "";
        if (StringUtils.startsWithIgnoreCase(os, "win")) {
            progam = path + "casperjs/bin/casperjs.exe";
            phantomJsPath = path + "phantomjs/window/phantomjs.exe";
        } else {
            progam = path + "casperjs/bin/casperjs";
            phantomJsPath = path + "phantomjs/linux/phantomjs";
        }
        logger.info("程序地址:{}", progam);
        ProcessBuilder processBuilder = new ProcessBuilder(progam, "geetest.js", pageUrl, deltaResolveAddress, " --web-security=no", "--ignore-ssl-errors=true");
        processBuilder.directory(new File(path + "casperjs/js"));
        processBuilder.environment().put("PHANTOMJS_EXECUTABLE", phantomJsPath);
        try {
            Process p = processBuilder.start();
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sbf = new StringBuffer();
            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                sbf.append(tmp).append("\r\n");
            }
            System.out.println(sbf.toString());
            if (sbf.indexOf("验证通过") >= 0)
                return true;
            return false;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int totalCount = 10;
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

    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static boolean startIdentification() {
        Future<Boolean> future = threadPool.submit(new Callable<Boolean>() {
                                                       @Override
                                                       public Boolean call() throws Exception {
                                                           return GeetestCaptchaIdentification.process("http://user.geetest.com/login?url=http:%2F%2Faccount.geetest.com%2Freport","http://localhost:8086/worker/resolveGeetestSlicePosition");
                                                       }
                                                   }
        );
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }
}

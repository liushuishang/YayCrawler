package yaycrawler.spider.downloader;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
public class PhantomJsMockDonwnloader extends AbstractDownloader {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static Pattern INVOKE_PATTERN = Pattern.compile("(\\w+)\\((.*)\\)");

    @Override
    public Page download(Request request, Task task) {
        Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String, String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200);
        }
        Page page = null;
        String cookie = String.valueOf(site.getHeaders().get("Cookie"));
        String domain = String.valueOf(site.getDomain());

        logger.info("downloading page {}", request.getUrl());
//        PageInfo pageInfo = (PageInfo) request.getExtra("$pageInfo");
//        String expression = pageInfo.getPageValidationRule().replaceAll(":eq\\(", ":nth-child(").replaceAll(" ", "%20");
//        String[] invokeArray = expression.split("\\)\\.");
//        for (int i = 0; i < invokeArray.length; i++) {
//            int j = 3;
//            String invokeStr = invokeArray[i];
//            if (!invokeStr.endsWith(")")) invokeStr += ")";
//
//            Matcher matcher = INVOKE_PATTERN.matcher(invokeStr);
//            if (matcher.find()) {
//                String methodName = matcher.group(1);
//                if (StringUtils.isBlank(methodName)) continue;
//
//                String[] paramArray = null;
//                String param = matcher.group(2);
//                if (param != null) {
////                    param = param.replaceAll("\"([^\"]*)\"", "$1");//去掉双引号
//                    paramArray = param.split("\\$\\$");
//                }
//                if (paramArray == null) {
//                    paramArray = new String[1];
//                    paramArray[0] = param;
//                }
//                if (StringUtils.indexOfAny(methodName.toLowerCase(), new String[]{"xpath", "css"}) > -1) {
//                    logger.info("开始执行函数:{},参数为:{}", methodName, JSON.toJSONString(paramArray));
//
//                    }
//
//                }
//            }
        int i =1;
        while (i > 0) {
            try {
                Process p = null;//这里我的codes.js是保存在c盘下面的phantomjs目录
                Properties prop = System.getProperties();
                String os = prop.getProperty("os.name");
                logger.info("路径 {}" + JSON.toJSONString(this.getClass().getResource("/")));
                String path = this.getClass().getResource("/").getPath();
                path = path.substring(1, path.lastIndexOf("/") + 1);
                String progam ;
                String phantomJsPath = "";
                if (StringUtils.startsWithIgnoreCase(os, "win")) {
                    progam = path + "casperjs/bin/casperjs.exe";
                    phantomJsPath = path + "phantomjs/window/phantomjs.exe";
                } else {
                    progam = path + "casperjs/bin/casperjs";
                    phantomJsPath = path + "phantomjs/linux/phantomjs";
                }
                logger.info("程序地址:{}", progam);
                ProcessBuilder processBuilder = new ProcessBuilder(progam, "casperjsDownload.js", request.getUrl(), "css", URLEncoder.encode("css", "utf-8"), domain, URLEncoder.encode(cookie, "utf-8"));
                processBuilder.directory(new File(path + "phantomjs/js"));
                processBuilder.environment().put("PHANTOMJS_EXECUTABLE", phantomJsPath);

                p = processBuilder.start();
                InputStream is = p.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String tmp = "";
                while ((tmp = br.readLine()) != null) {
                    sbf.append(tmp);
                }
                p.destroy();
                if (sbf.indexOf("页面解析失败") > -1) {
                    page = null;
                    continue;
                }
                page = handleResponse(request, sbf.toString());
                onSuccess(request);
                break;
            } catch (Exception e) {
                logger.warn("download page " + request.getUrl() + " error", e);
                if (site.getCycleRetryTimes() > 0) {
                    return addToCycleRetry(request, site);
                }
                onError(request);
                return null;
            } finally {
                i--;
            }
        }
        return page;
    }

    protected Page handleResponse(Request request, String content) throws IOException {
        Page page = new Page();
        page.setRawText(content);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(200);
        return page;
    }

    @Override
    public void setThread(int threadNum) {

    }
}

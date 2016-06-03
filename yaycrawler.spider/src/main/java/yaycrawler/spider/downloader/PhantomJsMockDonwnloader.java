package yaycrawler.spider.downloader;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
public class PhantomJsMockDonwnloader extends AbstractDownloader {

    private Logger logger = LoggerFactory.getLogger(getClass());

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

        logger.info("downloading page {}", request.getUrl());
        Page page = null;
        String cookie = String.valueOf(site.getHeaders().get("Cookie"));
        String domain = String.valueOf(site.getDomain());
        int i = 3;
        while (i > 0) {
            Process p = null;//这里我的codes.js是保存在c盘下面的phantomjs目录
            logger.info("路径 {}" + JSON.toJSONString(this.getClass().getResource("/")));
            String path =this.getClass().getResource("/").getPath();
            path = path.substring(1,path.lastIndexOf("/")+1);
            String progam =  path + "phantomjs/window/phantomjs";
            /**
             * 程序地址：
             * 执行功能的js
             * 域名
             * cookie
             */
            Properties prop = System.getProperties();
            String os = prop.getProperty("os.name");
            if(StringUtils.startsWithIgnoreCase(os,"win")) {
                progam =  path + "phantomjs/window/phantomjs";
            } else {
                progam =  path + "phantomjs/linux/phantomjs";
            }
            ProcessBuilder processBuilder = new ProcessBuilder(progam,"phantomDownload.js",request.getUrl(),domain,cookie);
            processBuilder.directory(new File(path+"phantomjs"));
            try {
                p = processBuilder.start();
                InputStream is = p.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String tmp = "";
                while ((tmp = br.readLine()) != null) {
                    sbf.append(tmp);
                }
                if(sbf.indexOf("{\"contentType\":null,\"headers\":[]") > -1) {
                    page = null;
                    continue;
                }
                page = handleResponse(request,sbf.toString());
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
                i --;
            }

        }
        return page;
    }

    protected Page handleResponse(Request request,String content) throws IOException {
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

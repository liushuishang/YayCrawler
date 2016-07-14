package yaycrawler.spider.downloader;

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
import yaycrawler.common.utils.CasperjsProgramManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
public class PhantomJsMockDonwnloader extends AbstractDownloader {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Page download(Request request, Task task, String cookie) {
        Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        Set<Integer> acceptStatCode;
//        String charset = "utf-8";
//        Map<String, String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
//            charset = site.getCharset();
//            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200, 500);
        }
        String domain = String.valueOf(site.getDomain());
        logger.debug("downloading page {}", request.getUrl());
        int statusCode = 0;
        String result = null;
        try {

            List<String> paramList = new ArrayList<>();
            paramList.add(request.getUrl());
            paramList.add(URLEncoder.encode(site.getUserAgent().replaceAll(" ", "%20"), "utf-8"));
            paramList.add(domain);
            paramList.add(String.valueOf(site.isUseGzip()));
            paramList.add(String.valueOf(site.getRetryTimes()));
            cookie = cookie != null ? URLEncoder.encode(cookie.replaceAll(" ", "%20"), "utf-8") : "";
            paramList.add(cookie);

            result = CasperjsProgramManager.launch("casperjsDownload.js", site.getCharset(), paramList);

            statusCode = Integer.parseInt(StringUtils.substringBefore(result, "\r\n").trim());
            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, result);
                onSuccess(request);
                return page;
            } else {
                logger.warn("code error {}\t,{}", statusCode, request.getUrl());
                return null;
            }
        } catch (Exception e) {
            logger.warn("download page {} error {} msg {}", request.getUrl(), e, result);
            if (site.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, site);
            }
            onError(request);
            return null;
        } finally {
            request.putExtra(Request.STATUS_CODE, statusCode);
        }
    }

    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(statusCode);
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
    public Page download(Request request, Task task) {
        return download(request, task, null);
    }

    @Override
    public void setThread(int threadNum) {

    }
}

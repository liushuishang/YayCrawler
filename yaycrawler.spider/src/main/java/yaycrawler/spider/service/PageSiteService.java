package yaycrawler.spider.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Site;
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.dao.domain.PageSite;
import yaycrawler.dao.repositories.PageSiteRepository;

import java.util.Map;

/**
 * Created by yuananyun on 2016/5/2.
 */
@Service
public class PageSiteService {
    @Autowired
    private PageSiteRepository siteRepository;

    public Site getSite(String domain) {
        return getSite(domain, false);
    }

    /**
     * 获取Site
     *
     * @param domain
     * @param needProxy 是否需要切换代理
     * @return
     */
    public Site getSite(String domain, boolean needProxy) {
        Site site = Site.me();
        site.setSleepTime(500);
        site.setDomain(domain);
        site.addHeader("host", domain);
        site.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
        site.setCharset("utf8");

        PageSite pageSite = siteRepository.findByDomain(domain);
        if (pageSite != null) {
            site.setDomain(pageSite.getDomain());
            site.setCharset(pageSite.getCharset());
            site.setCycleRetryTimes(pageSite.getCycleRetryTimes());
            site.setRetryTimes(pageSite.getRetryTimes());
            int sleepTime = pageSite.getSleepTime();
            site.setSleepTime(sleepTime);
            site.setUserAgent(pageSite.getUserAgent());
            site.setUseGzip(true);

            if (StringUtils.isNotEmpty(pageSite.getHeaders())) {
                try {
                    Map<String, String> headMap = JSON.parseObject(pageSite.getHeaders(), Map.class);
                    for (Map.Entry<String, String> entry : headMap.entrySet()) {
                        site.addHeader(entry.getKey(), entry.getValue());
                    }
                }catch (Exception ex) {}
            }
            if (!StringUtils.isBlank(pageSite.getDefaultCookies())) {
                try {
                Map<String, String> cookiesMap = JSON.parseObject(pageSite.getDefaultCookies(), Map.class);
                for (Map.Entry<String, String> entry : cookiesMap.entrySet()) {
                    site.addCookie(domain, entry.getKey(), entry.getValue());
                }
                }catch (Exception ex) {}
            }
//            //只设置一个有效Cookie即可
//            if (pageSite.getCookieList() != null)
//                for (SiteCookie cookie : pageSite.getCookieList())
//                    if ("1".equals(cookie.getAvailable())) {
//                        site.addHeader("Cookie", cookie.getCookie());
//                        //记录当前使用的cookie信息以便后续进行cookie刷新
//                        site.addCookie(cookie.getId(), cookie.getCookie());
//                        break;
//                    }
        }
        return site;
    }



    public PageSite getPageSiteByUrl(String pageUrl) {
        String domain = UrlUtils.getDomain(pageUrl);
        if (StringUtils.isBlank(domain)) return null;
        return siteRepository.findByDomain(domain);
    }


}

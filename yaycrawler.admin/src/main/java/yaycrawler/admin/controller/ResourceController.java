package yaycrawler.admin.controller;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import yaycrawler.admin.service.CrawlerResultRetrivalService;
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.dao.domain.SiteCookie;
import yaycrawler.dao.repositories.SiteCookieRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/18.
 */
@RestController
public class ResourceController {

    @Autowired
    private SiteCookieRepository cookieRepository;

    @Autowired
    private CrawlerResultRetrivalService resultRetrivalService;

    @RequestMapping(value = "/addCookie", method = RequestMethod.POST)
    public Object addCookie(String siteId, String domain, String cookie) {
        Assert.notNull(siteId);
        Assert.notNull(domain);
        Assert.notNull(cookie);
        return cookieRepository.save(new SiteCookie(siteId, domain, cookie));
    }

    @RequestMapping(value = "/deleteCookieByIds", method = RequestMethod.POST)
    public Object deleteCookieByIds(@RequestBody List<String> deletedIds) {
        if (deletedIds == null || deletedIds.size() == 0) return false;
            for (String deletedId : deletedIds) {
                cookieRepository.delete(deletedId);
            }
        return true;
    }

    @RequestMapping(value = "/queryQueueByName",method =RequestMethod.POST )
    @ResponseBody
    public Object viewCrawlerResult(@RequestBody Map map)
    {
        String pageUrl = MapUtils.getString(map, "pageUrl");
        String taskId = MapUtils.getString(map, "taskId");
        Assert.notNull(pageUrl);
        Assert.notNull(taskId);

        return resultRetrivalService.RetrivalByTaskId(UrlUtils.getDomain(pageUrl).replace(".","_"), taskId);
    }

}

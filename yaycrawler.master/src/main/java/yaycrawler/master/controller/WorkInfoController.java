package yaycrawler.master.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.master.service.WorkInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Administrator on 2016/5/11.
 */
@RestController
@RequestMapping(value = "/work")
public class WorkInfoController {

    @Autowired
    private WorkInfoService workInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/regeditWork",method = RequestMethod.POST)
    public Object regeditWork(HttpServletRequest request,
                          HttpServletResponse response,@RequestBody CrawlerRequest params) {
        Object data = workInfoService.regeditWork(params);
        return data;
    }

    @RequestMapping(value = "/regeditWorks",method = RequestMethod.POST)
    public Object regeditWork(HttpServletRequest request,
                              HttpServletResponse response,@RequestBody List<CrawlerRequest> crawlerRequests) {
        Object data = workInfoService.regeditWorks(crawlerRequests);
        return data;
    }

    @RequestMapping(value = "/listWorks")
    public Object listWorks(HttpServletRequest request,HttpServletResponse response, Integer count) {
        List<CrawlerRequest> crawlerRequests = workInfoService.listWorks(count);
        return crawlerRequests;
    }

    @RequestMapping(value = "/clearWorks")
    public Object clearWorks(HttpServletRequest request,HttpServletResponse response,@RequestBody List<CrawlerRequest> crawlerRequests) {
        Object data = workInfoService.removeCrawler(null);
        return data;
    }

}

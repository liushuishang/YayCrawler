package yaycrawler.master.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import yaycrawler.common.model.CrawRequest;
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
                          HttpServletResponse response,@RequestBody CrawRequest params) {
        Object data = workInfoService.regeditWork(params);
        return data;
    }

    @RequestMapping(value = "/regeditWorks",method = RequestMethod.POST)
    public Object regeditWork(HttpServletRequest request,
                              HttpServletResponse response,@RequestBody List<CrawRequest> crawRequests) {
        Object data = workInfoService.regeditWorks(crawRequests);
        return data;
    }

    public Object getWork() {

        return null;
    }


}

package yaycrawler.master.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import yaycrawler.common.model.CrawRequest;
import yaycrawler.master.model.WorkInfo;
import yaycrawler.master.service.WorkInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping(value = "/regedit",method = RequestMethod.POST)
    public Object regedit(HttpServletRequest request,
                          HttpServletResponse response,@RequestBody WorkInfo params) {
        Object data = workInfoService.regedit(params,request);
        return data;
    }

    @RequestMapping(value = "/regeditWork",method = RequestMethod.POST)
    public Object regeditWork(HttpServletRequest request,
                          HttpServletResponse response,@RequestBody CrawRequest params) {
        Object data = workInfoService.regeditWork(params,request);
        return data;
    }

    public Object getWork() {

        return null;
    }


}

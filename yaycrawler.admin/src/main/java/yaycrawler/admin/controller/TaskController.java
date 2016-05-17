package yaycrawler.admin.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import yaycrawler.admin.communication.MasterActor;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.utils.UrlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Controller
public class TaskController {

    @Autowired
    private MasterActor masterActor;

    @RequestMapping(value = "/publishTask", method = RequestMethod.GET)
    public ModelAndView getPublishTask() {
        return new ModelAndView("task_publish");
    }

    @RequestMapping(value = "/publishTask", method = RequestMethod.POST)
    @ResponseBody
    public Object postPublishTask(@RequestBody Map taskMap) {
        String url = MapUtils.getString(taskMap, "pageUrl");
        String method = MapUtils.getString(taskMap, "method");
        Assert.notNull(url);
        Assert.notNull(method);

        String paramJson = MapUtils.getString(taskMap, "urlParamsJson");
        Map<String, Object> data = new HashMap<>();
        if (!StringUtils.isBlank(paramJson)) {
            data = JSON.parseObject(paramJson, Map.class);
        }
        CrawlerRequest crawlerRequest = new CrawlerRequest(url, UrlUtils.getDomain(url), method.toUpperCase());
        crawlerRequest.setData(data);

        return masterActor.publishTasks(crawlerRequest);
    }

}

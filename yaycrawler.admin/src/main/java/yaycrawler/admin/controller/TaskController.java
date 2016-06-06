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
import yaycrawler.admin.service.CrawlerResultRetrivalService;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.TasksResult;
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

    @Autowired
    private CrawlerResultRetrivalService resultRetrivalService;

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

    @RequestMapping("/successQueueManagement")
    public ModelAndView successQueueManagement() {
        ModelAndView modelAndView = new ModelAndView("successqueue_management");
        modelAndView.addObject("queue", "success");
        return modelAndView;
    }

    @RequestMapping("/failQueueManagement")
    public ModelAndView failQueueManagement() {
        ModelAndView modelAndView = new ModelAndView("failqueue_management");
        modelAndView.addObject("queue", "fail");
        return modelAndView;
    }

    @RequestMapping("/itemQueueManagement")
    public ModelAndView itemQueueManagement() {
        ModelAndView modelAndView = new ModelAndView("itemqueue_management");
        modelAndView.addObject("queue", "item");
        return modelAndView;
    }

    @RequestMapping("/runningQueueManagement")
    public ModelAndView runningQueueManagement() {
        ModelAndView modelAndView = new ModelAndView("runningqueue_management");
        modelAndView.addObject("queue", "running");
        return modelAndView;
    }


    @RequestMapping("/queryQueueByName")
    @ResponseBody
    public Object queryQueueByName(TasksResult tasksResult) {
        Object data = null;
        String name = tasksResult.getName();
        if (StringUtils.equalsIgnoreCase(name, "fail")) {
            data = masterActor.retrievedFailQueueRegistrations(tasksResult);
        } else if (StringUtils.equalsIgnoreCase(name, "success")) {
            data = masterActor.retrievedSuccessQueueRegistrations(tasksResult);
        } else if (StringUtils.equalsIgnoreCase(name, "item")) {
            data = masterActor.retrievedItemQueueRegistrations(tasksResult);
        } else if (StringUtils.equalsIgnoreCase(name, "running")) {
            data = masterActor.retrievedRunningQueueRegistrations(tasksResult);
        }
        return data;
    }

    @RequestMapping(value = "/viewCrawlerResult", method = RequestMethod.POST)
    @ResponseBody
    public Object viewCrawlerResult(@RequestBody Map map) {
        String pageUrl = MapUtils.getString(map, "pageUrl");
        String taskId = MapUtils.getString(map, "taskId");
        Assert.notNull(pageUrl);
        Assert.notNull(taskId);

        return resultRetrivalService.RetrivalByTaskId(UrlUtils.getDomain(pageUrl).replace(".", "_"), taskId);
    }
}

package yaycrawler.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.model.TasksResult;
import yaycrawler.master.model.MasterContext;
import yaycrawler.master.service.CrawlerQueueService;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/12.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CrawlerQueueService crawlerQueueService;

    @RequestMapping("/registerQueues")
    @ResponseBody
    public RestFulResult acceptAdminTask(@RequestBody List<CrawlerRequest> crawlerRequests)
    {
        Boolean flag = crawlerQueueService.regeditQueues(crawlerRequests,true);
        if(flag)
            return RestFulResult.success(flag);
        else
            return RestFulResult.failure(flag.toString());
    }

    @RequestMapping(value = "/retrievedWorkerRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public Object retrievedWorkerRegistrations()
    {
        return RestFulResult.success(MasterContext.workerRegistrationMap.values());
    }

    @RequestMapping(value = "/retrievedSuccessQueueRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult retrievedSuccessQueueRegistrations(@RequestBody TasksResult tasksResult)
    {
        return RestFulResult.success(crawlerQueueService.queryQueues(tasksResult));
    }

    @RequestMapping(value = "/retrievedFailQueueRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult retrievedFailQueueRegistrations(@RequestBody TasksResult tasksResult)
    {
        return RestFulResult.success(crawlerQueueService.queryQueues(tasksResult));
    }

    @RequestMapping(value = "/retrievedRunningQueueRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult retrievedRunningQueueRegistrations(@RequestBody TasksResult tasksResult)
    {
        return RestFulResult.success(crawlerQueueService.queryQueues(tasksResult));
    }

    @RequestMapping(value = "/retrievedItemQueueRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult retrievedItemQueueRegistrations(@RequestBody TasksResult tasksResult)
    {
        return RestFulResult.success(crawlerQueueService.queryQueues(tasksResult));
    }

}

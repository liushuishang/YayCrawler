package yaycrawler.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
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
        return RestFulResult.success(crawlerQueueService.regeditQueues(crawlerRequests));
    }

    @RequestMapping(value = "/retrievedWorkerRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public Object retrievedWorkerRegistrations()
    {
        return RestFulResult.success(MasterContext.workerRegistrationMap.values());
    }

    @RequestMapping(value = "/retrievedSuccessQueueRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult retrievedSuccessQueueRegistrations()
    {
        return RestFulResult.success(crawlerQueueService.queryQueues("success_data"));
    }

    @RequestMapping(value = "/retrievedFailQueueRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult retrievedFailQueueRegistrations()
    {
        return RestFulResult.success(crawlerQueueService.queryQueues("fail_data"));
    }

    @RequestMapping(value = "/retrievedRunningQueueRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult retrievedRunningQueueRegistrations()
    {
        return RestFulResult.success(crawlerQueueService.queryQueues("running_data"));
    }

    @RequestMapping(value = "/retrievedItemQueueRegistrations",method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult retrievedItemQueueRegistrations()
    {
        return RestFulResult.success(crawlerQueueService.queryQueues("item_data"));
    }

}

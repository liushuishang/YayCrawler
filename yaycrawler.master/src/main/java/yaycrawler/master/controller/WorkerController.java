package yaycrawler.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.model.WorkerHeartbeat;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.master.communication.WorkerActor;
import yaycrawler.master.model.MasterContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ucs_yuananyun on 2016/5/12.
 */
@Controller
@RequestMapping(value = "/worker",produces = "application/json;charset=UTF-8")
public class WorkerController {

    @Autowired
    private WorkerActor workerActor;


    /**
     * worker注册
     *
     * @param registration
     * @return
     */
    @RequestMapping("/register")
    @ResponseBody
    public RestFulResult register(HttpServletRequest request,@RequestBody WorkerRegistration registration) {
        Assert.notNull(registration.getWorkerId());
        MasterContext.registeWorker(registration);
        return RestFulResult.success(true);
    }

    /**
     * worker心跳
     *
     * @param heartbeat
     * @return
     */
    @RequestMapping("/heartBeat")
    @ResponseBody
    public RestFulResult heartBeat(HttpServletRequest request,@RequestBody WorkerHeartbeat heartbeat) {
        Assert.notNull(heartbeat.getWorkerId());
        MasterContext.receiveWorkerHeartbeat(heartbeat);
        return RestFulResult.success(true);
    }


    @RequestMapping("/crawlerSuccessNotify")
    @ResponseBody
    public RestFulResult crawlerSuccessNotify(HttpServletRequest request,@RequestBody CrawlerResult crawlerResult) {
        Assert.notNull(crawlerResult);

        //TODO 把结果加入队列中
        workerActor.assignTasks(crawlerResult.getCrawlerRequestList());

        return RestFulResult.success(true);
    }

    @RequestMapping("/crawlerFailureNotify")
    @ResponseBody
    public RestFulResult crawlerFailureNotify(HttpServletRequest request,@RequestBody CrawlerResult crawlerResult) {
        Assert.notNull(crawlerResult);

        //TODO 任务执行失败的处理
        return RestFulResult.success(true);
    }



}

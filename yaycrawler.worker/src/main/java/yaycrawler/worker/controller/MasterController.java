package yaycrawler.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.worker.service.TaskScheduleService;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Controller
@RequestMapping(value = "/master", produces = {"application/json; charset=UTF-8"})
public class MasterController {

    @Autowired
    private TaskScheduleService taskScheduleService;

    @RequestMapping(value = "assignTasks", method = RequestMethod.POST)
    @ResponseBody
    public RestFulResult assignTasks(@RequestBody List<CrawlerRequest> taskList) {
        if (taskList == null || taskList.size() == 0) return RestFulResult.failure("任务列表不能为空！");

        taskScheduleService.doSchedule(taskList);
        return RestFulResult.success(true);
    }

}

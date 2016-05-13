package yaycrawler.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import yaycrawler.common.model.CrawRequest;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.worker.service.TaskScheduleService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Controller
@RequestMapping("/master")
public class MasterController {

    @Autowired
    private TaskScheduleService taskScheduleService;

    @RequestMapping("assignTasks")
    public RestFulResult assignTasks(HttpServletRequest request, List<CrawRequest> taskList) {
        if (taskList == null || taskList.size() == 0) return RestFulResult.failure("任务列表不能为空！");

        taskScheduleService.doSchedule(taskList);
        return RestFulResult.success(true);
    }

}

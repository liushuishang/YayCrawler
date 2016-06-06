package yaycrawler.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import yaycrawler.quartz.service.QuartzScheduleService;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
@Controller
public class JobController {

    @Autowired
    private QuartzScheduleService quartzScheduleService;


    @RequestMapping("/viewPlannedJobs")
    public ModelAndView viewScheduledJobs() {
        return new ModelAndView("planned_job_list");
    }

    @RequestMapping("/viewRunningJobs")
    public ModelAndView viewRunningJobs() {
        return new ModelAndView("running_job_list");
    }

    @RequestMapping(value = "/getPlannedJobList", method = RequestMethod.GET)
    @ResponseBody
    public Object getPlannedJobList() {
        return quartzScheduleService.getScheduledJobList();
    }

    @RequestMapping(value = "/getRunningJobList", method = RequestMethod.GET)
    @ResponseBody
    public Object getScheduledJobList() {
        return quartzScheduleService.getRunningJobList();
    }

    @RequestMapping("/addScheduleJob")
    public ModelAndView addScheduleJob() {
        return new ModelAndView("add_schedule_job");
    }

    @RequestMapping(value = "/saveScheduleJob", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView saveScheduleJob() {

        return null;
    }

}

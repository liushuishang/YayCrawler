package yaycrawler.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import yaycrawler.admin.communication.MasterActor;

/**
 * Created by yuananyun on 2016/5/3.
 */
@Controller
public class HomeController {
    @Autowired
    private MasterActor masterActor;

    @RequestMapping({"", "/", "/index"})
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping(value = {"/workerList"}, method = RequestMethod.GET)
    public ModelAndView workerList() {
        ModelAndView mv = new ModelAndView("worker_list");
        return mv;
    }
    @RequestMapping(value = {"/queryWorkers"}, method = RequestMethod.GET)
    @ResponseBody
    public Object queryWorkers()
    {
        return masterActor.retrievedWorkerRegistrations();
    }

}

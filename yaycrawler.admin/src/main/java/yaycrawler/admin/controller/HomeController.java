package yaycrawler.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by yuananyun on 2016/5/3.
 */
@Controller
public class HomeController {

    @RequestMapping({"","/","/index"})
    public ModelAndView index()
    {
        return new ModelAndView("index");
    }

}

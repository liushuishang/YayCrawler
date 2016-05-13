package yaycrawler.master.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2016/5/11.
 */

@RestController
@RequestMapping(value = "/home",produces = "application/json;charset=UTF-8")
public class HomeController {

    @RequestMapping(value = {"/", "/test"}, method = RequestMethod.GET)
    public Object test() {
        return "test";
    }

}
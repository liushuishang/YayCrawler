package yaycrawler.master.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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

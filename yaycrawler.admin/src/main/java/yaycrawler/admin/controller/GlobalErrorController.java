package yaycrawler.admin.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;

/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Controller
public class GlobalErrorController implements ErrorController {
    @Override
    public String getErrorPath() {
        return "error";
    }
}

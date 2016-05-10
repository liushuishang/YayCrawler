package yaycrawler.admin.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;

/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
public class GlobalErrorController implements ErrorController {
    @Override
    public String getErrorPath() {
        return null;
    }
}

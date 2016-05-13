package yaycrawler.worker.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by ucs_yuananyun on 2016/3/17.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException(Exception ex) {
        return redirectToErrorPage("可能是数据源拒绝了数据请求！");
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ModelAndView handleSocketTimeoutException(Exception ex) {
        return redirectToErrorPage("连接超时，请检查您的网络！");
    }

    @ExceptionHandler(UnknownHostException.class)
    public ModelAndView handleUnknownHostException(Exception ex) {
        return redirectToErrorPage("网络不通，请检查您的网络！");
    }

    private ModelAndView redirectToErrorPage(String message) {
        ModelAndView model = new ModelAndView("error/generic_error");
        model.addObject("error", message);
        return model;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex) {
        String message = ex.getMessage();
        if (message.contains("SocketTimeoutException"))
            return handleSocketTimeoutException(ex);
        if (message.contains("UnknownHostException"))
            return handleUnknownHostException(ex);
        if (message.contains("IOException")) {
            return handleIOException(ex);
        }

        return redirectToErrorPage( message);
    }


}

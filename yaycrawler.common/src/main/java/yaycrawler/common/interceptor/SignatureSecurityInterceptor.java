package yaycrawler.common.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import yaycrawler.common.utils.SignatureUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/19.
 */
public class SignatureSecurityInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(SignatureSecurityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String signature = request.getParameter("signature");

        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getSession(true).getServletContext());
        String secret = context.getEnvironment().getProperty("signature.token");

        if (StringUtils.isBlank(nonce) || StringUtils.isBlank(timestamp) || StringUtils.isBlank(signature)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.error("{} 是一个非法请求", request.getRequestURI());
            return false;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("timestamp", timestamp);
        paramMap.put("nonce", nonce);
        paramMap.put("secret", secret);

        String localSignature = SignatureUtils.signWithSHA1(paramMap);
        if (!signature.equals(localSignature)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            logger.error("{} 签名不匹配", request.getRequestURI());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

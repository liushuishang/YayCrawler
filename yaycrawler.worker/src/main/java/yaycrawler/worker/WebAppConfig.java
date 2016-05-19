package yaycrawler.worker;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import yaycrawler.common.interceptor.SignatureSecurityInterceptor;

/**
 * Created by ucs_yuananyun on 2016/5/19.
 */
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SignatureSecurityInterceptor()).addPathPatterns("/master/**");
    }
}

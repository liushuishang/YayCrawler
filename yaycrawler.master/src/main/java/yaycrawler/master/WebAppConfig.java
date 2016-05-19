package yaycrawler.master;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import yaycrawler.master.communication.SignatureSecurityInterceptor;

/**
 * Created by ucs_yuananyun on 2016/5/19.
 */
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SignatureSecurityInterceptor()).addPathPatterns("/admin/**","/worker/**");
    }
}

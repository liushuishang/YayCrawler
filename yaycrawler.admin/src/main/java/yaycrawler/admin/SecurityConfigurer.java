//package yaycrawler.admin;
//
//import org.springframework.boot.autoconfigure.security.SecurityProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
///**
// * Created by ucs_yuananyun on 2016/6/2.
// */
//@Configuration
//@EnableWebSecurity
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
//        builder.inMemoryAuthentication()
//                .withUser("user").password("user").roles("USER")
//                .and().withUser("admin").password("admin").roles("ADMIN");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
//    }
//}

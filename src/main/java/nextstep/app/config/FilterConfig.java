package nextstep.app.config;

import jakarta.servlet.Filter;
import nextstep.security.BasicAuthenticationFilter;
import nextstep.security.LoginFormAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> basicAuthenticationFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>(new BasicAuthenticationFilter());

        registrationBean.addUrlPatterns("/members");

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> loginFormAuthenticationFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>(new LoginFormAuthenticationFilter());

        registrationBean.addUrlPatterns("/login");

        return registrationBean;
    }
}

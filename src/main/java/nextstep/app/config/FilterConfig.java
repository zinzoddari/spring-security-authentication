package nextstep.app.config;

import jakarta.servlet.Filter;
import nextstep.security.BasicAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> BasicAuthenticationFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new BasicAuthenticationFilter());

        registrationBean.addUrlPatterns("/members");

        return registrationBean;
    }
}

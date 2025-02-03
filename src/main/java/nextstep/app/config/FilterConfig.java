package nextstep.app.config;

import nextstep.security.BasicAuthenticationFilter;
import nextstep.security.DefaultSecurityFilterChain;
import nextstep.security.FilterChainProxy;
import nextstep.security.LoginFormAuthenticationFilter;
import nextstep.security.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.List;

@Configuration
class FilterConfig {

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy() {
        SecurityFilterChain securityFilterChain = new DefaultSecurityFilterChain(
                List.of(new BasicAuthenticationFilter()
                , new LoginFormAuthenticationFilter())
        );

        List<SecurityFilterChain> filterChains = List.of(securityFilterChain);

        return new DelegatingFilterProxy(new FilterChainProxy(filterChains));
    }
}

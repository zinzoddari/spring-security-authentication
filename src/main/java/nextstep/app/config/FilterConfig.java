package nextstep.app.config;

import nextstep.app.MemberService;
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

    private final MemberService memberService;

    public FilterConfig(MemberService memberService) {
        this.memberService = memberService;
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy() {
        return new DelegatingFilterProxy(new FilterChainProxy(List.of(securityFilterChain())));
    }

    @Bean
    public SecurityFilterChain securityFilterChain() {
        return new DefaultSecurityFilterChain(
                List.of(new BasicAuthenticationFilter(memberService)
                        , new LoginFormAuthenticationFilter(memberService))
        );
    }
}

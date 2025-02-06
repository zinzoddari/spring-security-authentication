package nextstep.app.config;

import java.util.List;
import nextstep.app.MemberService;
import nextstep.security.BasicAuthenticationFilter;
import nextstep.security.DefaultSecurityFilterChain;
import nextstep.security.FilterChainProxy;
import nextstep.security.LoginFormAuthenticationFilter;
import nextstep.security.SecurityFilterChain;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextHolderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

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
            List.of(
                new SecurityContextHolderFilter(new HttpSessionSecurityContextRepository())
                , new BasicAuthenticationFilter(memberService)
                , new LoginFormAuthenticationFilter(memberService)
            )
        );
    }
}

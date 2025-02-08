package nextstep.app.config;

import java.util.List;
import nextstep.app.MemberService;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.filter.BasicAuthenticationFilter;
import nextstep.security.authentication.DaoAuthenticationProvider;
import nextstep.security.filter.DefaultSecurityFilterChain;
import nextstep.security.filter.FilterChainProxy;
import nextstep.security.filter.LoginFormAuthenticationFilter;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.filter.SecurityFilterChain;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContextHolderFilter;
import nextstep.security.filter.converter.BasicAuthenticationConverter;
import nextstep.security.filter.converter.LoginFormAuthenticationConverter;
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
                , new BasicAuthenticationFilter(authenticationManager(), new BasicAuthenticationConverter())
                , new LoginFormAuthenticationFilter(authenticationManager(), new LoginFormAuthenticationConverter())
            )
        );
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(new DaoAuthenticationProvider(memberService));
    }
}

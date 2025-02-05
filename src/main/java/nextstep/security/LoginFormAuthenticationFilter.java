package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.domain.Authentication;
import nextstep.security.domain.AuthenticationException;
import nextstep.security.domain.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.Map;

public class LoginFormAuthenticationFilter implements Filter {

    private final UserDetailsService userDetailsService;

    public LoginFormAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String authorization = ((HttpServletRequest) request).getHeader("Authorization");

        if (!isDefaultAuthentication(authorization)) {
            return;
        }

        Map<String, String[]> parameterMap = request.getParameterMap();

        final String username = parameterMap.get("username")[0];
        final String password = parameterMap.get("password")[0];

        AuthenticationManager authenticationManager = new ProviderManager(new DaoAuthenticationProvider(userDetailsService));
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException();
        }

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private boolean isDefaultAuthentication(final String authorization) {
        return authorization == null;
    }
}

package nextstep.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.authentication.AuthenticationException;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.authentication.Authentication;
import nextstep.security.filter.converter.AuthenticationConverter;

import java.io.IOException;

public class LoginFormAuthenticationFilter implements Filter {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationConverter authenticationConverter;

    public LoginFormAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationConverter authenticationConverter) {
        this.authenticationManager = authenticationManager;
        this.authenticationConverter = authenticationConverter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isAuthenticated()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            final Authentication authentication = authenticationConverter.convert((HttpServletRequest) request);

            final Authentication authenticatedAuthentication = authenticationManager.authenticate(authentication);

            if (!authenticatedAuthentication.isAuthenticated()) {
                throw new AuthenticationException();
            }

            saveAuthentication(authenticatedAuthentication);
        } catch (RuntimeException e) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(401);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * SecurityContext에 정보가 유효한지 확인합니다.
     */
    private boolean isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 기본, Login Form 형식의 인증 정보인지 확인합니다.
     */
    private boolean isDefaultAuthentication(final String authorization) {
        return authorization == null;
    }

    /**
     * 인증 된 authorization 객체를, context에 저장합니다.
     */
    private void saveAuthentication(final Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}

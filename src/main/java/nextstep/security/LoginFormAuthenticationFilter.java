package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.domain.Authentication;
import nextstep.security.domain.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.Map;

public class LoginFormAuthenticationFilter implements Filter {

    public static final String AUTHORIZATION = "Authorization";

    private final UserDetailsService userDetailsService;

    public LoginFormAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isAuthenticated()) {
            chain.doFilter(request, response);
            return;
        }

        final String authorization = ((HttpServletRequest) request).getHeader(AUTHORIZATION);

        if (!isDefaultAuthentication(authorization)) {
            chain.doFilter(request, response);
            return;
        }

        final Map<String, String[]> parameterMap = request.getParameterMap();
        Authentication authentication = null;

        try {
            authentication = authenticateUser(parameterMap);
        } catch (RuntimeException e) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(401);
            return;
        }

        if (!authentication.isAuthenticated()) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(401);
            return;
        }

        saveAuthentication(authentication);

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
     * 인증이 유효한 Authentication 객체를 반환합니다.
     */
    private Authentication authenticateUser(final Map<String, String[]> parameterMap) {
        String username;
        String password;

        try {
            username = parameterMap.get("username")[0];
            password = parameterMap.get("password")[0];
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }

        final AuthenticationManager authenticationManager = new ProviderManager(new DaoAuthenticationProvider(userDetailsService));

        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
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

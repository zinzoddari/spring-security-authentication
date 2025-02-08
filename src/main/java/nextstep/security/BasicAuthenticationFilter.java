package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.util.Base64Convertor;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.domain.Authentication;

import java.io.IOException;
import nextstep.security.domain.AuthenticationException;
import nextstep.security.domain.UsernamePasswordAuthenticationToken;

public class BasicAuthenticationFilter implements Filter {

    public static final String AUTHORIZATION = "Authorization";
    private static final String BASIC_AUTH_PREFIX = "Basic ";

    private final AuthenticationManager authenticationManager;

    public BasicAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isAuthenticated()) {
            chain.doFilter(request, response);
            return;
        }

        final String authorization = ((HttpServletRequest) request).getHeader(AUTHORIZATION);

        if (!isBasicAuthentication(authorization)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication;

        try {
            authentication = authenticateUser(authorization);

            if (!authentication.isAuthenticated()) {
               throw new AuthenticationException();
            }
        } catch (IllegalArgumentException | AuthenticationException e) {
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
     * Basic 형식의 인증 정보인지 확인합니다.
     */
    private boolean isBasicAuthentication(final String authorization) {
        return authorization != null && authorization.startsWith(BASIC_AUTH_PREFIX);
    }

    /**
     * 인증이 유효한 Authentication 객체를 반환합니다.
     */
    private Authentication authenticateUser(final String authorization) throws IllegalArgumentException {
        final String[] usernameAndPassword = getUsernameAndPassword(authorization);
        final String username = usernameAndPassword[0];
        final String password = usernameAndPassword[1];

        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    /**
     * authorization 정보 기반으로, username과 password를 반환합니다.
     */
    private String[] getUsernameAndPassword(final String authorization) {
        final String credentials = authorization.split(" ")[1];
        final String decodedString = Base64Convertor.decode(credentials);

        final String[] usernameAndPassword = decodedString.split(":");

        if (usernameAndPassword.length != 2) {
            throw new IllegalArgumentException();
        }

        return decodedString.split(":");
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

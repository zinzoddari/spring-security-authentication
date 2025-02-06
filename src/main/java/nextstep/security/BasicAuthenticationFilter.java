package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
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

    private final UserDetailsService userDetailsService;

    public BasicAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
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

        final Authentication authentication = authenticateUser(authorization);
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
    private Authentication authenticateUser(final String authorization) {
        final String[] usernameAndPassword = getUsernameAndPassword(authorization);

        final AuthenticationManager authenticationManager = new ProviderManager(new DaoAuthenticationProvider(userDetailsService));
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameAndPassword[0], usernameAndPassword[1]));

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException();
        }

        return authentication;
    }

    /**
     * authorization 정보 기반으로, username과 password를 반환합니다.
     */
    private String[] getUsernameAndPassword(final String authorization) {
        final String credentials = authorization.split(" ")[1];
        final String decodedString = Base64Convertor.decode(credentials);

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

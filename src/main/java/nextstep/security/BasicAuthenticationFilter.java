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

    private final UserDetailsService userDetailsService;

    public BasicAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        final String authorization = ((HttpServletRequest) request).getHeader("Authorization");

        if (!isBasicAuthentication(authorization)) {
            chain.doFilter(request, response);
            return;
        }

        String credentials = authorization.split(" ")[1];
        String decodedString = Base64Convertor.decode(credentials);
        String[] usernameAndPassword = decodedString.split(":");

        AuthenticationManager authenticationManager = new ProviderManager(new DaoAuthenticationProvider(userDetailsService));
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameAndPassword[0], usernameAndPassword[1]));

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException();
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private boolean isBasicAuthentication(final String authorization) {
        return authorization != null && authorization.startsWith("Basic ");
    }
}

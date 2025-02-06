package nextstep.security.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityContextHolderFilter implements Filter {

    private final SecurityContextRepository securityContextRepository;

    public SecurityContextHolderFilter(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        SecurityContext securityContext = securityContextRepository.loadContext((HttpServletRequest) servletRequest);

        if (securityContext != null) {
            SecurityContextHolder.setContext(securityContext);
        }

        filterChain.doFilter(servletRequest, servletResponse);

        SecurityContext afterSecurityContext = SecurityContextHolder.getContext();
        securityContextRepository.saveContext(afterSecurityContext, (HttpServletRequest) servletRequest,
            (HttpServletResponse) servletResponse);

        SecurityContextHolder.clearContext();
    }
}

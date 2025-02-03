package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

public class FilterChainProxy implements Filter {

    private final List<SecurityFilterChain> filterChains;

    public FilterChainProxy(List<SecurityFilterChain> filterChains) {
        this.filterChains = filterChains;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String authorization = ((HttpServletRequest) request).getHeader("Authorization");

        SecurityFilterChain filterChain = filterChains.get(0);

        for (Filter filter : filterChain.getFilter()) {
            if (authorization != null && !authorization.isEmpty() && filter.getClass() == BasicAuthenticationFilter.class) {
                filter.doFilter(request, response, chain);
                return;
            }

            if (!request.getParameterMap().isEmpty() && filter.getClass() == LoginFormAuthenticationFilter.class) {
                filter.doFilter(request, response, chain);
                return;
            }
        }
    }
}

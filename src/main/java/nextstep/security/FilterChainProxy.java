package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import nextstep.security.context.SecurityContextHolder;

public class FilterChainProxy implements Filter {

    private final List<SecurityFilterChain> filterChains;

    public FilterChainProxy(List<SecurityFilterChain> filterChains) {
        this.filterChains = filterChains;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityFilterChain filterChain = filterChains.get(0);
        
        for (Filter filter : filterChain.getFilter()) {
            try {
                filter.doFilter(request, response, chain);
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                ((HttpServletResponse) response).setStatus(401);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

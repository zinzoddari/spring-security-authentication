package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.domain.Authentication;

import java.io.IOException;
import java.util.Map;

public class LoginFormAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String authorization = ((HttpServletRequest) request).getHeader("Authorization");

        if (!isDefaultAuthentication(authorization)) {
            return;
        }

        Map<String, String[]> parameterMap = request.getParameterMap();

        final String username = parameterMap.get("username")[0];
        final String password = parameterMap.get("password")[0];

        request.setAttribute("Authentication", new Authentication(username, password));
    }

    private boolean isDefaultAuthentication(final String authorization) {
        return authorization == null;
    }
}

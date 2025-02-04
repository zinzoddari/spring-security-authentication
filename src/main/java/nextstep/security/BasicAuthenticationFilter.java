package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import nextstep.app.util.Base64Convertor;
import nextstep.security.domain.Authentication;

import java.io.IOException;

public class BasicAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String authorization = ((HttpServletRequest) request).getHeader("Authorization");

        if (!isBasicAuthentication(authorization)) {
            return;
        }

        String credentials = authorization.split(" ")[1];
        String decodedString = Base64Convertor.decode(credentials);
        String[] usernameAndPassword = decodedString.split(":");

        request.setAttribute("Authentication", new Authentication(usernameAndPassword[0], usernameAndPassword[1]));
    }

    private boolean isBasicAuthentication(final String authorization) {
        return authorization != null && authorization.startsWith("Basic ");
    }
}

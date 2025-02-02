package nextstep.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.app.util.Base64Convertor;
import nextstep.security.domain.Authentication;

import java.io.IOException;

public class BasicAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String authorization = ((HttpServletRequest) request).getHeader("Authorization");

        String credentials = authorization.split(" ")[1];
        String decodedString = Base64Convertor.decode(credentials);
        String[] usernameAndPassword = decodedString.split(":");

        try {
            request.setAttribute("Authentication", new Authentication(usernameAndPassword[0], usernameAndPassword[1]));
        } catch (RuntimeException e) {
            ((HttpServletResponse) response).setStatus(401);
            return;
        }

        chain.doFilter(request, response);
    }
}

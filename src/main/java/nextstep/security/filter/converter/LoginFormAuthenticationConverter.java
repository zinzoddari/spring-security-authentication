package nextstep.security.filter.converter;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Map;

public class LoginFormAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(final HttpServletRequest request) {
        final Map<String, String[]> parameterMap = request.getParameterMap();

        String username;
        String password;

        try {
            username = parameterMap.get("username")[0];
            password = parameterMap.get("password")[0];
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }

        return new UsernamePasswordAuthenticationToken(username, password);
    }
}

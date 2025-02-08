package nextstep.security.filter.converter;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.app.util.Base64Convertor;
import nextstep.security.authentication.Authentication;
import nextstep.security.authentication.UsernamePasswordAuthenticationToken;

public class BasicAuthenticationConverter implements AuthenticationConverter {

    private static final String AUTHORIZATION = "Authorization";

    @Override
    public Authentication convert(HttpServletRequest request) {
        final String authorization = request.getHeader(AUTHORIZATION);

        final String username;
        final String password;

        try {
            final String credentials = authorization.split("Basic ")[1];
            final String decodedString = Base64Convertor.decode(credentials);

            final String[] usernameAndPassword = decodedString.split(":");

            username = usernameAndPassword[0];
            password = usernameAndPassword[1];
        } catch (RuntimeException e) {
            throw new IllegalArgumentException();
        }

        return new UsernamePasswordAuthenticationToken(username, password);
    }
}

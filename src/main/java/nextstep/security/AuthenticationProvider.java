package nextstep.security;

import nextstep.security.domain.Authentication;
import nextstep.security.domain.AuthenticationException;

public interface AuthenticationProvider {

    Authentication authenticate(Authentication authentication) throws AuthenticationException;

    boolean supports(Class<?> authentication);
}

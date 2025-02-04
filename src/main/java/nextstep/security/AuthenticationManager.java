package nextstep.security;

import nextstep.security.domain.Authentication;

public interface AuthenticationManager {
    Authentication authenticate(Authentication authentication);
}

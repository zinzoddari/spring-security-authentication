package nextstep.security;

import nextstep.security.domain.UserDetails;

public interface UserDetailsService {

    UserDetails findByUsername(String username);
}

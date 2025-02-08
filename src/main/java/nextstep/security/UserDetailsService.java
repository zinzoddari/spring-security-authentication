package nextstep.security;

public interface UserDetailsService {

    UserDetails findByUsername(String username);
}

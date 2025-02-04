package nextstep.security;

import nextstep.security.domain.Authentication;
import nextstep.security.domain.AuthenticationException;
import nextstep.security.domain.UserDetails;
import nextstep.security.domain.UsernamePasswordAuthenticationToken;

public class DaoAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public DaoAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.findByUsername((String) authentication.getPrincipal());

        if (userDetails == null) {
            return authentication;
        }

        return new UsernamePasswordAuthenticationToken(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

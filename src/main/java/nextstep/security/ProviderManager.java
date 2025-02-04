package nextstep.security;

import nextstep.security.domain.Authentication;

public class ProviderManager implements AuthenticationManager {

    private final AuthenticationProvider authenticationProvider;

    public ProviderManager(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!authenticationProvider.supports(authentication.getClass())) {
            return authentication;
        }

        return authenticationProvider.authenticate(authentication);
    }
}

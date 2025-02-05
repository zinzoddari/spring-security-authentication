package nextstep.security.context;

import nextstep.security.domain.Authentication;

public class SecurityContextImpl implements SecurityContext {

    private Authentication authentication;

    @Override
    public Authentication getAuthentication() {
        return null;
    }

    @Override
    public void setAuthentication(final Authentication authentication) {
        this.authentication = authentication;
    }
}

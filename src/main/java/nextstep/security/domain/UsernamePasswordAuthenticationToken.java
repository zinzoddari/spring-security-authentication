package nextstep.security.domain;

public class UsernamePasswordAuthenticationToken implements Authentication {

    private final Object principal;
    private final Object credentials;
    private final boolean isAuthenticated;

    public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        this.principal = principal;
        this.credentials = credentials;
        this.isAuthenticated = false;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}

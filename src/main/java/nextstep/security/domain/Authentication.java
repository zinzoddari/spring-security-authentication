package nextstep.security.domain;

public interface Authentication {

    Object getPrincipal();
    Object getCredentials();
    boolean isAuthenticated();
}

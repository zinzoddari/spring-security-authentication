package nextstep.app.support;

public interface Authentication {
    Object getPrincipal();

    Object getCredentials();

    boolean isAuthenticated();
}

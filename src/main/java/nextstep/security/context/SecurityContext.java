package nextstep.security.context;

import nextstep.security.domain.Authentication;

public interface SecurityContext {

    Authentication getAuthentication();

    void setAuthentication(Authentication authentication);
}

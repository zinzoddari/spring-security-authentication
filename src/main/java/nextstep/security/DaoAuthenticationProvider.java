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
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        UserDetails userDetails;

        try {
            userDetails = userDetailsService.findByUsername((String) authentication.getPrincipal());
        } catch (AuthenticationException e) {
            userDetails = null;
        }

        if (userDetails == null) {
            return authentication;
        }

        if (!matchesPassword(userDetails, authentication)) {
            return authentication;
        }

        return new UsernamePasswordAuthenticationToken(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 등록 된 회원 정보의 비밀번호가, 입력된 비밀번호가 일치하는지 확인합니다.
     */
    private boolean matchesPassword(final UserDetails userDetails, final Authentication authentication) {
        return userDetails.getPassword().equals(authentication.getCredentials());
    }
}

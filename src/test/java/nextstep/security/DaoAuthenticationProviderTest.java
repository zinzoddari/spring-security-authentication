package nextstep.security;

import nextstep.security.domain.Authentication;
import nextstep.security.domain.UserDetails;
import nextstep.security.domain.UsernamePasswordAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DaoAuthenticationProviderTest {

    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Mock
    private UserDetailsService userDetailsService;

    final String username = "TEST-USERNAME";

    final String password = "TEST-PASSWORD";

    @BeforeEach
    void init() {
        daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
    }

    @Test
    @DisplayName("회원 정보가 유효하지 않을 때, 인증 실패")
    void isNull() {
        //given

        final Authentication input = new UsernamePasswordAuthenticationToken(username, password);

        given(userDetailsService.findByUsername(username)).willReturn(null);

        //when
        final Authentication result = daoAuthenticationProvider.authenticate(input);

        //then
        assertSoftly(it -> {
            it.assertThat(result.isAuthenticated()).isFalse();
            it.assertThat(result.getPrincipal()).isEqualTo(username);
            it.assertThat(result.getCredentials()).isEqualTo(password);
        });
    }

    @Test
    @DisplayName("비밀번호가 유효하지 않으면, 인증 실패")
    void passwordIsNotMatches() {
        //given
        final Authentication input = new UsernamePasswordAuthenticationToken(username, password);

        given(userDetailsService.findByUsername(username)).willReturn(비밀번호가_유효하지_않은_UserDetails_반환());

        //when
        final Authentication result = daoAuthenticationProvider.authenticate(input);

        //then
        assertSoftly(it -> {
            it.assertThat(result.isAuthenticated()).isFalse();
            it.assertThat(result.getPrincipal()).isEqualTo(username);
            it.assertThat(result.getCredentials()).isEqualTo(password);
        });
    }

    @Test
    @DisplayName("인증 정보가 유효한 경우, 인증 성공")
    void isValid() {
        //given
        final Authentication input = new UsernamePasswordAuthenticationToken(username, password);

        given(userDetailsService.findByUsername(username)).willReturn(유효한_UserDetails_반환());

        //when
        final Authentication result = daoAuthenticationProvider.authenticate(input);

        //then
        assertSoftly(it -> {
            it.assertThat(result.isAuthenticated()).isTrue();
            it.assertThat(result.getPrincipal()).isEqualTo(username);
            it.assertThat(result.getCredentials()).isEqualTo(password);
        });
    }

    private UserDetails 유효한_UserDetails_반환() {
        return new UserDetails() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };
    }

    private UserDetails 비밀번호가_유효하지_않은_UserDetails_반환() {
        return new UserDetails() {
            @Override
            public String getUsername() {
                return null;
            }

            @Override
            public String getPassword() {
                return "INVALID-PASSWORD";
            }
        };
    }
}

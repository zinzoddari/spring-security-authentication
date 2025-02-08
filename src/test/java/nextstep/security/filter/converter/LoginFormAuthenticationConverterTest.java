package nextstep.security.filter.converter;

import nextstep.security.authentication.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginFormAuthenticationConverterTest {

    private LoginFormAuthenticationConverter loginFormAuthenticationConverter;

    @BeforeEach
    void init() {
        loginFormAuthenticationConverter = new LoginFormAuthenticationConverter();
    }

    @Test
    @DisplayName("성공적으로 Authentication로 변환합니다.")
    void valid() {
        //given
        final String username = "TEST-USERNAME";
        final String password = "TEST-PASSWORD";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", username);
        request.setParameter("password", password);

        //when
        final Authentication authentication = loginFormAuthenticationConverter.convert(request);

        //then
        assertSoftly(it -> {
            it.assertThat(authentication).isNotNull();
            it.assertThat(authentication.getPrincipal()).isEqualTo(username);
            it.assertThat(authentication.getCredentials()).isEqualTo(password);
            it.assertThat(authentication.isAuthenticated()).isFalse();
        });
    }

    @Nested
    @DisplayName("입력 값이 없을 때,")
    class invalidInput {

        @Test
        @DisplayName("username가 없어 실패합니다.")
        void invalidUsername() {
            //given
            final String password = "TEST-PASSWORD";

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("password", password);

            //when & then
            assertThrows(IllegalArgumentException.class, () -> loginFormAuthenticationConverter.convert(request));
        }

        @Test
        @DisplayName("password가 없어 실패합니다.")
        void invalidPassword() {
            //given
            final String password = "TEST-PASSWORD";

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("password", password);

            //when & then
            assertThrows(IllegalArgumentException.class, () -> loginFormAuthenticationConverter.convert(request));
        }
    }
}

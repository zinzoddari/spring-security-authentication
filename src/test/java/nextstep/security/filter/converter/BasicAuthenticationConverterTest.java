package nextstep.security.filter.converter;

import nextstep.app.util.Base64Convertor;
import nextstep.security.authentication.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BasicAuthenticationConverterTest {

    private BasicAuthenticationConverter authenticationConverter;

    @BeforeEach
    void init() {
        authenticationConverter = new BasicAuthenticationConverter();
    }

    @Test
    @DisplayName("성공적으로 Authentication로 변환합니다.")
    void valid() {
        //given
        final String username = "TEST-USERNAME";
        final String password = "TEST-PASSWORD";

        MockHttpServletRequest request = new MockHttpServletRequest();
        final String authorization = String.join(":", username, password);
        final String encodeAuthorization = Base64Convertor.encode(authorization);
        request.addHeader("Authorization", String.join(" ", "Basic", encodeAuthorization));

        //when
        final Authentication authentication = authenticationConverter.convert(request);

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
        @DisplayName("입력값이 누락되어 실패합니다.")
        void invalidUsername() {
            //given
            final String password = "TEST-PASSWORD";

            MockHttpServletRequest request = new MockHttpServletRequest();
            final String authorization = String.join(":", password);
            final String encodeAuthorization = Base64Convertor.encode(authorization);
            request.addHeader("Authorization", String.join(" ", "Basic", encodeAuthorization));
            request.setParameter("password", password);

            //when & then
            assertThrows(IllegalArgumentException.class, () -> authenticationConverter.convert(request));
        }

        @Test
        @DisplayName(": 이 포함되지 않아 실패합니다.")
        void invalidPassword() {
            //given
            final String separator = "?";
            final String username = "TEST-USERNAME";
            final String password = "TEST-PASSWORD";

            MockHttpServletRequest request = new MockHttpServletRequest();
            final String authorization = String.join(separator, username, password);
            final String encodeAuthorization = Base64Convertor.encode(authorization);
            request.addHeader("Authorization", String.join(" ", "Basic", encodeAuthorization));

            //when & then
            assertThrows(IllegalArgumentException.class, () -> authenticationConverter.convert(request));
        }
    }
}

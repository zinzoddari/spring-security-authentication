package nextstep.app;

import jakarta.servlet.http.HttpSession;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.authentication.Authentication;
import nextstep.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FormLoginTest {
    private final Member TEST_MEMBER = new Member("a@a.com", "password", "a", "");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(TEST_MEMBER);
    }

    @DisplayName("로그인 성공")
    @Test
    void login_success() throws Exception {
        ResultActions loginResponse = mockMvc.perform(post("/login")
                .param("username", TEST_MEMBER.getEmail())
                .param("password", TEST_MEMBER.getPassword())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        loginResponse.andExpect(status().isOk());

        HttpSession session = loginResponse.andReturn().getRequest().getSession();
        Authentication authentication = (Authentication) session.getAttribute("SPRING_SECURITY_CONTEXT");

        assertSoftly(it -> {
            it.assertThat(session).isNotNull();
            it.assertThat(authentication).isNotNull();
            it.assertThat(authentication.getPrincipal()).isEqualTo(TEST_MEMBER.getEmail());
            it.assertThat(authentication.getCredentials()).isEqualTo(TEST_MEMBER.getPassword());
        });
    }

    @DisplayName("로그인 실패 - 사용자 없음")
    @Test
    void login_fail_with_no_user() throws Exception {
        ResultActions response = mockMvc.perform(post("/login")
                .param("username", "none")
                .param("password", "none")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        response.andExpect(status().isUnauthorized());
    }

    @DisplayName("로그인 실패 - 비밀번호 불일치")
    @Test
    void login_fail_with_invalid_password() throws Exception {
        ResultActions response = mockMvc.perform(post("/login")
                .param("username", TEST_MEMBER.getEmail())
                .param("password", "invalid")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        response.andExpect(status().isUnauthorized());
    }

    @Nested
    @DisplayName("로그인 실패 - 입력 값 누락")
    class invalidInputValue {

        @Test
        @DisplayName("아이디가 없어 로그인 실패")
        void idIsNull() throws Exception {
            //when
            ResultActions response = mockMvc.perform(post("/login")
                    .param("password", "invalid")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            );

            //then
            response.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("비밀번호가 없어 로그인 실패")
        void passwordIsNull() throws Exception {
            ResultActions response = mockMvc.perform(post("/login")
                    .param("username", TEST_MEMBER.getEmail())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            );

            response.andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("로그인 후 세션을 통해 회원 목록 조회")
    @Test
    void login_after_members() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .param("username", TEST_MEMBER.getEmail())
                        .param("password", TEST_MEMBER.getPassword())
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                ).andDo(print())
                .andExpect(status().isOk());

        MvcResult membersResponse = mockMvc.perform(get("/members")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        final List<Member> response = TestUtils.toDtoList(membersResponse.getResponse().getContentAsString(), Member.class);

        assertSoftly(it -> {
            it.assertThat(response).size().isEqualTo(1);
            it.assertThat(response.get(0).getEmail()).isEqualTo(TEST_MEMBER.getEmail());
            it.assertThat(response.get(0).getPassword()).isEqualTo(TEST_MEMBER.getPassword());
            it.assertThat(response.get(0).getName()).isEqualTo(TEST_MEMBER.getName());
            it.assertThat(response.get(0).getImageUrl()).isEqualTo(TEST_MEMBER.getImageUrl());
        });
    }
}

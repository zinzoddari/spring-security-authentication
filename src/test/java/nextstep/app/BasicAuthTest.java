package nextstep.app;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.util.Base64Convertor;
import nextstep.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BasicAuthTest {
    private final Member TEST_MEMBER = new Member("a@a.com", "password", "a", "");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(TEST_MEMBER);
    }

    @DisplayName("Basic Auth 인증 성공 후 회원 목록 조회")
    @Test
    void members() throws Exception {
        String token = TEST_MEMBER.getEmail() + ":" + TEST_MEMBER.getPassword();
        String encoded = Base64Convertor.encode(token);

        MvcResult loginResponse = mockMvc.perform(get("/members")
                .header("Authorization", "Basic " + encoded)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        ).andDo(print())
                        .andExpect(status().isOk())
                                .andReturn();

        final List<Member> response = TestUtils.toDtoList(loginResponse.getResponse().getContentAsString(), Member.class);

        assertSoftly(it -> {
            it.assertThat(response).size().isEqualTo(1);
            it.assertThat(response.get(0).getEmail()).isEqualTo(TEST_MEMBER.getEmail());
            it.assertThat(response.get(0).getPassword()).isEqualTo(TEST_MEMBER.getPassword());
            it.assertThat(response.get(0).getName()).isEqualTo(TEST_MEMBER.getName());
            it.assertThat(response.get(0).getImageUrl()).isEqualTo(TEST_MEMBER.getImageUrl());
        });
    }

    @DisplayName("Basic Auth 인증 실패 시 에러 응답")
    @Test
    void members_fail() throws Exception {
        ResultActions loginResponse = mockMvc.
                perform(get("/members")
                        .header("Authorization", "Basic " + "invalid")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                );

        loginResponse.andDo(print());
        loginResponse.andExpect(status().isUnauthorized());
    }
}

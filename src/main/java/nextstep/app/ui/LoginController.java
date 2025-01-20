package nextstep.app.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    private final MemberRepository memberRepository;

    public LoginController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(HttpServletRequest request, HttpSession session) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String username = parameterMap.get("username")[0];
        String password = parameterMap.get("password")[0];

        Member member = memberRepository.findByEmail(username)
                .filter(it -> it.matchPassword(password))
                .orElseThrow(AuthenticationException::new);

        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, member);

        return ResponseEntity.ok().build();
    }
}

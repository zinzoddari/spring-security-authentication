package nextstep.app.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.domain.Authentication;
import nextstep.security.domain.UsernamePasswordAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    private final MemberRepository memberRepository;

    public LoginController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(HttpServletRequest request, HttpSession session) {
        final Authentication authentication = (Authentication) request.getAttribute("Authentication");

        Member member = memberRepository.findByEmail((String) authentication.getPrincipal())
                .filter(it -> it.matchPassword((String) authentication.getCredentials()))
                .orElseThrow(AuthenticationException::new);

        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, member);

        return ResponseEntity.ok().build();
    }
}

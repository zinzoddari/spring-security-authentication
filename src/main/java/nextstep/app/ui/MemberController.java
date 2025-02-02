package nextstep.app.ui;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.domain.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> list(HttpServletRequest request) {
        try {
            final Authentication userDetails = (Authentication) request.getAttribute("Authentication");
            memberRepository.findByEmail(userDetails.getUsername())
                    .filter(it -> it.matchPassword(userDetails.getPassword()))
                    .orElseThrow(AuthenticationException::new);

            List<Member> members = memberRepository.findAll();
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }
}

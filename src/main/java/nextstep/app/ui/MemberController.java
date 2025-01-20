package nextstep.app.ui;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.util.Base64Convertor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/members")
    public ResponseEntity<List<Member>> list(@RequestHeader("Authorization") String authorization) {
        try {
            String credentials = authorization.split(" ")[1];
            String decodedString = Base64Convertor.decode(credentials);
            String[] usernameAndPassword = decodedString.split(":");
            String username = usernameAndPassword[0];
            String password = usernameAndPassword[1];

            memberRepository.findByEmail(username)
                    .filter(it -> it.matchPassword(password))
                    .orElseThrow(AuthenticationException::new);

            List<Member> members = memberRepository.findAll();
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }
}

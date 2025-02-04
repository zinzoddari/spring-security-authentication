package nextstep.app;

import nextstep.app.domain.MemberRepository;
import nextstep.security.UserDetailsService;
import nextstep.security.domain.AuthenticationException;
import nextstep.security.domain.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails findByUsername(String username) {
        return memberRepository.findByEmail(username)
            .orElseThrow(AuthenticationException::new);
    }
}

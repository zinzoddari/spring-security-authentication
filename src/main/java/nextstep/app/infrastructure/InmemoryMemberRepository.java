package nextstep.app.infrastructure;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InmemoryMemberRepository implements MemberRepository {
    private final Map<String, Member> members = new HashMap<>();

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.ofNullable(members.get(email));
    }

    @Override
    public List<Member> findAll() {
        return members.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void save(Member member) {
        members.put(member.getEmail(), member);
    }
}

package roomescape.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = new Member(memberRequest.name(), memberRequest.email(), memberRequest.password(), "USER");
        Member savedMember = memberRepository.save(member);
        return new MemberResponse(savedMember.getId(), savedMember.getName(), savedMember.getEmail());
    }

    @Transactional(readOnly = true)
    public Member login(String email, String password) {
        return memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }
}

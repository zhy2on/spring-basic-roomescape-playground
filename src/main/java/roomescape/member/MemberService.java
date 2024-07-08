package roomescape.member;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.JwtUtils;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;

    public MemberService(MemberRepository memberRepository, JwtUtils jwtUtils) {
        this.memberRepository = memberRepository;
        this.jwtUtils = jwtUtils;
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

    public String createToken(Member member) {
        return jwtUtils.createToken(member);
    }

    @Transactional(readOnly = true)
    public Member findMemberByToken(String token) {
        try {
            Claims claims = jwtUtils.parseToken(token);
            Long id = Long.parseLong(claims.getSubject());
            String name = claims.get("name", String.class);
            String role = claims.get("role", String.class);

            return new Member(id, name, role);
        } catch (Exception e) {
            return null;
        }
    }
}

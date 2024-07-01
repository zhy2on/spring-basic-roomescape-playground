package roomescape.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Value("${roomescape.auth.jwt.secret}")
    private String secretKey;

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

    public String createToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim("name", member.getName())
                .claim("role", member.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    @Transactional(readOnly = true)
    public Member findMemberByToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String name = claims.get("name", String.class);
            return memberRepository.findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        } catch (Exception e) {
            return null;
        }
    }
}

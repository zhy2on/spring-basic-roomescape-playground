package roomescape.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MemberService {
    private MemberDao memberDao;

    @Value("${roomescape.auth.jwt.secret}")
    private String secretKey;

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberDao.save(new Member(memberRequest.name(), memberRequest.email(), memberRequest.password(), "USER"));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public String login(String email, String password) {
        Member member = memberDao.findByEmailAndPassword(email, password);
        if (member == null) {
            throw new RuntimeException("Invalid email or password");
        }
        return createToken(member);
    }

    private String createToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim("name", member.getName())
                .claim("role", member.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public MemberResponse getMemberInfoFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long memberId = Long.valueOf(claims.getSubject());
        String name = claims.get("name", String.class);

        return new MemberResponse(memberId, name, null);
    }
}

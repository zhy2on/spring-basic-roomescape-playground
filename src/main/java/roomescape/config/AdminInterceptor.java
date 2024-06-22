package roomescape.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.exception.ForbiddenException;
import roomescape.exception.UnauthorizedException;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.util.CookieUtil;

public class AdminInterceptor implements HandlerInterceptor {

    private final MemberService memberService;

    public AdminInterceptor(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = CookieUtil.extractTokenFromCookie(request.getCookies());

            if (token == null) {
                throw new UnauthorizedException("No token found in the request");
            }

            Member member = memberService.findMemberByToken(token);

            if (member == null) {
                throw new UnauthorizedException("Invalid token");
            }

            if (!"ADMIN".equals(member.getRole())) {
                throw new ForbiddenException("User does not have admin privileges");
            }

            return true;
        } catch (UnauthorizedException | ForbiddenException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            return false;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An unexpected error occurred");
            return false;
        }
    }
}

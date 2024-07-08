package roomescape.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.JwtUtils;
import roomescape.exception.ForbiddenException;
import roomescape.exception.UnauthorizedException;
import roomescape.util.CookieUtil;

public class AdminInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public AdminInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = CookieUtil.extractTokenFromCookie(request.getCookies());

            if (token == null) {
                throw new UnauthorizedException("No token found in the request");
            }

            String role = jwtUtils.getRoleFromToken(token);

            if (role == null) {
                throw new UnauthorizedException("Invalid token");
            }

            if (!"ADMIN".equals(role)) {
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

package roomescape.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.auth.JwtUtils;
import roomescape.member.LoginMember;
import roomescape.util.CookieUtil;
import io.jsonwebtoken.Claims;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtUtils jwtUtils;

    public LoginMemberArgumentResolver(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = CookieUtil.extractTokenFromCookie(request.getCookies());
        if (token != null && !token.isEmpty()) {
            try {
                Claims claims = jwtUtils.parseToken(token);
                Long id = Long.parseLong(claims.getSubject());
                String name = claims.get("name", String.class);
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);
                return new LoginMember(id, name, email, role);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}

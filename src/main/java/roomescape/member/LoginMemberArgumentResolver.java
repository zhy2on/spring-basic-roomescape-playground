package roomescape.member;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.util.CookieUtil;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private MemberService memberService;

    public LoginMemberArgumentResolver(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = CookieUtil.extractTokenFromCookie(request.getCookies());
        if (token != null && !token.isEmpty()) {
            Member member = memberService.findMemberByToken(token);
            if (member != null) {
                return new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
            }
        }
        return null;
    }
}

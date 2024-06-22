package roomescape.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
    public static String extractTokenFromCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

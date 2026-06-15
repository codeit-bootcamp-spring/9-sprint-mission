package com.sprint.mission.discodeit.utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtils {

    public static Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // 보통 토큰은 HttpOnly 설정 필수!
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
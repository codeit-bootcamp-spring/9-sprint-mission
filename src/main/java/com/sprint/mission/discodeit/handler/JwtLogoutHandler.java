package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.repository.JwtRegistry;
import com.sprint.mission.discodeit.security.RefreshTokenStore;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLogoutHandler implements LogoutHandler {

    private final RefreshTokenStore refreshTokenStore;
    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = getRefreshTokenFromCookie(request);
        refreshTokenStore.remove(refreshToken);
        log.info("리프레시 토큰 무효화 완료: {}", refreshToken);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/auth");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        Cache usersCache = cacheManager.getCache("users");
        if (usersCache != null) {
            usersCache.clear();
            log.debug("로그아웃 처리: 'users' 캐시 초기화 완료");
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

    }
}

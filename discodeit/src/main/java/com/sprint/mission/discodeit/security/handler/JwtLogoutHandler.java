package com.sprint.mission.discodeit.security.handler;

import com.sprint.mission.discodeit.security.registry.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

  public JwtLogoutHandler(JwtRegistry jwtRegistry, CacheManager cacheManager) {
    this.jwtRegistry = jwtRegistry;
    this.cacheManager = cacheManager;
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    if (cacheManager != null && cacheManager.getCache("allUsers") != null) {
      cacheManager.getCache("allUsers").evict("all");
      log.debug("로그아웃 처리 - 사용자 목록 캐시(allUsers)가 무효화되었습니다.");
    }

    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
          .findFirst()
          .ifPresent(cookie -> {
            String refreshToken = cookie.getValue();

            if (jwtRegistry instanceof InMemoryJwtRegistry inMemoryJwtRegistry) {
              inMemoryJwtRegistry.invalidateByRefreshToken(refreshToken);
            }
          });
    }

    Cookie cookie = new Cookie("REFRESH_TOKEN", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);

    response.addCookie(cookie);
    log.debug("로그아웃 처리 - REFRESH_TOKEN 쿠키가 삭제되었습니다.");
  }
}
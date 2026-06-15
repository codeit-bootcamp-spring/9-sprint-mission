package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.sse.SseService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;
  private final SseService sseService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      Arrays.stream(cookies)
          .filter(cookie -> JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME.equals(
              cookie.getName()))
          .findFirst()
          .ifPresent(cookie -> jwtRegistry.findByRefreshToken(cookie.getValue())
              .ifPresent(jwtInformation -> {
                jwtRegistry.invalidateJwtInformationByUserId(jwtInformation.userDto().id());
                sseService.broadcast("users.updated", withOnline(jwtInformation.userDto(), false));
              }));
    }
    evictUsersCache();

    Cookie refreshTokenCookie = new Cookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, null);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(0);
    response.addCookie(refreshTokenCookie);
  }

  private UserDto withOnline(UserDto userDto, boolean online) {
    return new UserDto(
        userDto.id(),
        userDto.username(),
        userDto.email(),
        userDto.profile(),
        online,
        userDto.role()
    );
  }

  private void evictUsersCache() {
    Cache cache = cacheManager.getCache(CacheConfig.USERS);
    if (cache != null) {
      cache.clear();
    }
  }
}

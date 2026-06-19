package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.sse.SseEventNames;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final SseService sseService;

  @Override
  @CacheEvict(cacheNames = CacheConfig.USERS, allEntries = true)
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
          .findFirst()
          .ifPresent(cookie -> jwtRegistry.invalidateJwtInformationByRefreshToken(cookie.getValue()));
    }
    if (authentication != null
        && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      sseService.broadcast(SseEventNames.USERS_UPDATED, userWithOnline(userDetails.getUserDto(), false));
    }

    ResponseCookie expiredRefreshTokenCookie = ResponseCookie
        .from(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, "")
        .path("/")
        .httpOnly(true)
        .sameSite("Lax")
        .maxAge(0)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshTokenCookie.toString());
  }

  private UserResponse userWithOnline(UserResponse user, boolean online) {
    return new UserResponse(
        user.id(),
        user.username(),
        user.email(),
        user.profile(),
        online,
        user.role()
    );
  }
}

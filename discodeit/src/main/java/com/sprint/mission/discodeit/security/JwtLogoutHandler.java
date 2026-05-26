package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    if (request.getCookies() == null) {
      return;
    }

    Arrays.stream(request.getCookies())
        .filter(cookie -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
        .findFirst()
        .ifPresent(cookie -> {
          try {
            var claims = jwtTokenProvider.validateAndGetClaims(cookie.getValue());
            jwtRegistry.invalidateJwtInformationByUserId(
                jwtTokenProvider.extractUserId(claims));
          } catch (Exception ignored) {
          }

          cookie.setValue("");
          cookie.setMaxAge(0);
          cookie.setPath("/");
          response.addCookie(cookie);
        });
  }
}

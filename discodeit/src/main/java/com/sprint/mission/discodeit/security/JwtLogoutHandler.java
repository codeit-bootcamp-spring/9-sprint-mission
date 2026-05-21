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

  private final JwtRegistry jwtRegistry;

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
              .ifPresent(jwtInformation -> jwtRegistry.invalidateJwtInformationByUserId(
                  jwtInformation.userDto().id())));
    }

    Cookie refreshTokenCookie = new Cookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, null);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(0);
    response.addCookie(refreshTokenCookie);
  }
}

package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;

  @Override
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

    ResponseCookie expiredRefreshTokenCookie = ResponseCookie
        .from(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, "")
        .path("/")
        .httpOnly(true)
        .sameSite("Lax")
        .maxAge(0)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshTokenCookie.toString());
  }
}

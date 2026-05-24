package com.sprint.mission.discodeit.security.handler;

import com.sprint.mission.discodeit.security.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    log.info("로그아웃 진행: REFRESH_TOKEN 쿠키 삭제");

    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
          .findFirst()
          .ifPresent(cookie -> {
            jwtRegistry.invalidateJwtInformationByRefreshToken(cookie.getValue());
          });
    }

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String accessToken = authHeader.substring(7);
      log.info("Access Token으로 레지스트리 삭제");
      jwtRegistry.invalidateJwtInformationByAccessToken(accessToken);
    }


    org.springframework.http.ResponseCookie deleteCookie =
        org.springframework.http.ResponseCookie.from("REFRESH_TOKEN", "")
            .path("/api/auth")
            .httpOnly(true)
            .maxAge(0)
            .sameSite("Lax")
            .build();

    response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, deleteCookie.toString());
  }
}
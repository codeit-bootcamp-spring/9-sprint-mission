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
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    log.info("[JwtLogoutHandler] 로그아웃 가동 - 리프레시 토큰 쿠키 및 장부 파기 프로세스 시작");

    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())

          .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
          .findFirst()
          .ifPresent(cookie -> {
            String refreshToken = cookie.getValue();

            if (jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {

              jwtRegistry.invalidateJwtInformationByRefreshToken(refreshToken);
            }
          });
    }

    Cookie cookie = new Cookie("REFRESH_TOKEN", null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);

    response.addCookie(cookie);
    log.info("[JwtLogoutHandler] REFRESH_TOKEN 쿠키 파기 및 서버 장부 소거 동기화 완료");
  }

  /**
   * ⚙️ 장부 내부를 전수 검사하여, 로그아웃을 요청한 토큰 찌꺼기를 안전하게 적출하는 서브루틴
   */
}
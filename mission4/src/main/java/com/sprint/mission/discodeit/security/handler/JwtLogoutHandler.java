package com.sprint.mission.discodeit.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtLogoutHandler implements LogoutHandler {

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    log.info("[JwtLogoutHandler] 로그아웃 가동 - 리프레시 토큰 쿠키 파기 프로세스 시작");

    Cookie cookie = new Cookie("REFRESH_TOKEN", null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);

    response.addCookie(cookie);
    log.info("[JwtLogoutHandler] REFRESH_TOKEN 쿠키 파기 완료");
  }
}
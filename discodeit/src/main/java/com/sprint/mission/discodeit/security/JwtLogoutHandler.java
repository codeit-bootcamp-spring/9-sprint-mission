package com.sprint.mission.discodeit.security;

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
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    log.info("로그아웃 진행: REFRESH_TOKEN 쿠키 삭제");

    Cookie cookie = new Cookie("REFRESH_TOKEN", null);

    cookie.setPath("/");

    cookie.setMaxAge(0);

    cookie.setHttpOnly(true);

    response.addCookie(cookie);
  }
}
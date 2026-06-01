package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtLogoutHandler implements LogoutHandler {

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // 쿠키에 저장된 REFRESH_TOKEN을 삭제
    // 삭제 방법: 같은 이름의 쿠키를 maxAge=0으로 덮어쓰면 브라우저가 즉시 삭제함
    Cookie deleteCookie = new Cookie("REFRESH_TOKEN", null);
    deleteCookie.setHttpOnly(true);
    deleteCookie.setPath("/");
    deleteCookie.setMaxAge(0); // 0초 = 즉시 만료
    response.addCookie(deleteCookie);
  }
}
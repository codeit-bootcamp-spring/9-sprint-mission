package com.sprint.mission.discodeit.secure.handler;

import com.sprint.mission.discodeit.repository.JwtSessionRepository;
import com.sprint.mission.discodeit.secure.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.secure.JwtInformation;
import com.sprint.mission.discodeit.secure.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtSessionRepository jwtSessionRepository;
  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    if (request.getCookies() == null) {
      return;
    }

    String refreshToken = Arrays.stream(request.getCookies())
        .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);

    if (refreshToken != null) {
      jwtSessionRepository.findByRefreshToken(refreshToken)
          .ifPresent(session -> {
            jwtRegistry.invalidateJwtInformationByUserId(session.getUserId());
            jwtSessionRepository.delete(session);
            log.info("JWT 세션 삭제 완료 - logout, userId: {}", session.getUserId());
          });
    }

    Cookie cookie = new Cookie("REFRESH_TOKEN", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);


  }

}

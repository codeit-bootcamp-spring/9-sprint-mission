package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.jwt.store.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

  private final RefreshTokenService refreshTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    if (request.getCookies() != null) {
      for (Cookie c : request.getCookies()) {
        if ("REFRESH_TOKEN".equals(c.getName())) {
          String token = c.getValue();
          if (token != null && !token.isBlank()) {
            try {
              UUID jti = jwtGetJtiSafe(token);
              if (jti != null) refreshTokenService.revoke(jti);
            } catch (Exception ignored) {
            }
          }
        }
      }
    }

    Cookie cookie = new Cookie("REFRESH_TOKEN", null);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // HTTPS only
    cookie.setMaxAge(0);
    cookie.setAttribute("SameSite", "Strict"); // CSRF protection
    response.addCookie(cookie);

    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  private UUID jwtGetJtiSafe(String token) {
    try {
      return jwtTokenProvider.getJti(token);
    } catch (Exception ex) {
      return null;
    }
  }
}





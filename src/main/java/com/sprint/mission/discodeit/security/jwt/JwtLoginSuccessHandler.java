package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.store.RefreshTokenService;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.store.JwtInformation;
import java.time.Instant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final JwtRegistry jwtRegistry;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      String accessToken = jwtTokenProvider.createAccessToken(userDetails);
      String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUserDto().id());

      // register in registry
      Instant now = Instant.now();
      Instant accessExpiresAt = now.plusSeconds(jwtTokenProvider.getAccessTokenValiditySeconds());
      Instant refreshExpiresAt = now.plusSeconds(jwtTokenProvider.getRefreshTokenValiditySeconds());
      JwtInformation info = new JwtInformation(userDetails.getUserDto(), accessToken, refreshToken, accessExpiresAt, refreshExpiresAt);
      jwtRegistry.registerJwtInformation(info);

      Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
      cookie.setHttpOnly(true);
      cookie.setSecure(true); // HTTPS only
      cookie.setPath("/");
      cookie.setMaxAge((int) jwtTokenProvider.getRefreshTokenValiditySeconds());
      cookie.setAttribute("SameSite", "Strict"); // CSRF protection
      response.addCookie(cookie);

      response.setStatus(HttpServletResponse.SC_OK);
      JwtDto jwtDto = new JwtDto(accessToken, "Bearer");
      response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(objectMapper.writeValueAsString(
          new RuntimeException("Authentication failed: invalid principal")
      ));
    }
  }
}

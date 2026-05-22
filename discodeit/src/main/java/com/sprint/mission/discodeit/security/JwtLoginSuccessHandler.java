package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  public static final String REFRESH_TOKEN_COOKIE_NAME = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME;

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    String accessToken = jwtTokenProvider.createToken(userDetails);
    String refreshToken = jwtTokenProvider.refreshToken(userDetails);
    Instant expiresAt = Instant.now().plusSeconds(jwtTokenProvider.getExpirationSeconds());
    jwtRegistry.registerJwtInformation(
        new JwtInformation(userDetails.getUserDto().id(), accessToken, refreshToken, expiresAt)
    );
    JwtDto body = new JwtDto(
        userDetails.getUserDto(),
        accessToken,
        "Bearer",
        expiresAt.toString()
    );

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie(refreshToken).toString());
    objectMapper.writeValue(response.getWriter(), body);
  }

  private ResponseCookie refreshTokenCookie(String refreshToken) {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
        .path("/")
        .httpOnly(true)
        .sameSite("Lax")
        .maxAge(jwtTokenProvider.getExpirationSeconds())
        .build();
  }
}

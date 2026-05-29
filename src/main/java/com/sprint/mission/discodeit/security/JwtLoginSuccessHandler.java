package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      String subject = userDetails.getUsername();  // email 또는 userId

      // 1. 토큰 발급
      String accessToken  = jwtTokenProvider.createAccessToken(subject);
      String refreshToken = jwtTokenProvider.createRefreshToken(subject);

      // 2. Refresh Token → HttpOnly 쿠키에 저장
      Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
      refreshCookie.setHttpOnly(true);   // JS에서 접근 불가 (XSS 방어)
      refreshCookie.setPath("/");        // 모든 경로에서 전송
      response.addCookie(refreshCookie);

      // 3. Access Token → 응답 Body에 포함
      response.setStatus(HttpServletResponse.SC_OK);
      JwtDto jwtDto = new JwtDto(accessToken);
      response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      ErrorResponse errorResponse = new ErrorResponse(
          new RuntimeException("Authentication failed: Invalid user details"),
          HttpServletResponse.SC_UNAUTHORIZED
      );
      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
  }
}

package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 14 * 24 * 60 * 60;

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      UserDto userDto = userDetails.getUserDto();
      String accessToken = jwtTokenProvider.createAccessToken(authentication);
      String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
      jwtRegistry.registerJwtInformation(new JwtInformation(
          userDto,
          accessToken,
          refreshToken,
          jwtTokenProvider.getExpirationTime(refreshToken)
      ));

      response.addCookie(createRefreshTokenCookie(refreshToken));
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write(objectMapper.writeValueAsString(new JwtDto(userDto, accessToken)));
      return;
    }

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ErrorResponse errorResponse = new ErrorResponse(
        new RuntimeException("Authentication failed: Invalid user details"),
        HttpServletResponse.SC_UNAUTHORIZED
    );
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }

  private Cookie createRefreshTokenCookie(String refreshToken) {
    Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
    return cookie;
  }
}

package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.sprint.mission.discodeit.config.CacheConfig;

@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  public static final String REFRESH_TOKEN_COOKIE_NAME = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME;

  private final ObjectMapper objectMapper;
  private final JwtTokenIssuer jwtTokenIssuer;

  @Override
  @CacheEvict(cacheNames = CacheConfig.USERS, allEntries = true)
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    JwtIssue jwtIssue = jwtTokenIssuer.issue(userDetails);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtIssue.accessToken());
    response.addHeader(HttpHeaders.SET_COOKIE, jwtIssue.refreshTokenCookie().toString());
    objectMapper.writeValue(response.getWriter(), jwtIssue.body());
  }
}

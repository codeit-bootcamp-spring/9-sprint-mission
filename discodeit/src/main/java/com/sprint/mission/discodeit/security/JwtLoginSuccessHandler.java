package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.sse.SseEventNames;
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
  private final SseService sseService;

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
    sseService.broadcast(SseEventNames.USERS_UPDATED, userWithOnline(userDetails.getUserDto(), true));
  }

  private UserResponse userWithOnline(UserResponse user, boolean online) {
    return new UserResponse(
        user.id(),
        user.username(),
        user.email(),
        user.profile(),
        online,
        user.role()
    );
  }
}

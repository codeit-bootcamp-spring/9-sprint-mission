package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    log.info("로그인 실패 - 예외: {}", exception.getMessage());

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    ErrorResponse errorResponse = new ErrorResponse(Instant.now(),ErrorCode.INVALID_USER_CREDENTIALS.name(), ErrorCode.INVALID_USER_CREDENTIALS.getMessage(), Map.of(), exception.getClass().getSimpleName(), HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}

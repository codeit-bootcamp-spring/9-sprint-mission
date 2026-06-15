package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

    log.info("로그인 성공 - 사용자: {}", userDetails.getUsername());

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");

    String result = objectMapper.writeValueAsString(userDetails.getUserDto());
    response.getWriter().write(result);
  }
}

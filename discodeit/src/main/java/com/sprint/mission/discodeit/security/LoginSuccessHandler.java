package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
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
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      response.setStatus(HttpServletResponse.SC_OK);
      UserDto userDto = userDetails.getUserDto();
      response.getWriter().write(objectMapper.writeValueAsString(userDto));

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

package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final ObjectMapper objectMapper;

  public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    if (!isJson(request)) {
      return super.attemptAuthentication(request, response);
    }

    LoginRequest loginRequest = readLoginRequest(request);
    if (loginRequest.isRememberMe()) {
      request.setAttribute("remember-me", true);
    }
    UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken
        .unauthenticated(loginRequest.username(), loginRequest.password());
    setDetails(request, authRequest);
    return this.getAuthenticationManager().authenticate(authRequest);
  }

  private boolean isJson(HttpServletRequest request) {
    String contentType = request.getContentType();
    return StringUtils.hasText(contentType) && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE);
  }

  private LoginRequest readLoginRequest(HttpServletRequest request) {
    try {
      return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    } catch (IOException e) {
      throw new AuthenticationServiceException("Invalid login request", e);
    }
  }

  private record LoginRequest(
      String username,
      String password,
      @JsonAlias("remember-me") Boolean rememberMe) {

    private boolean isRememberMe() {
      return Boolean.TRUE.equals(rememberMe);
    }

  }
}

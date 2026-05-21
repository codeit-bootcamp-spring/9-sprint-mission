package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    String accessToken = jwtTokenProvider.createToken(userDetails);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("userDto", userDetails.getUserDto());
    body.put("user", userDetails.getUserDto());
    body.put("accessToken", accessToken);
    body.put("tokenType", "Bearer");
    body.put("expiresAt", Instant.now()
        .plusSeconds(jwtTokenProvider.getExpirationSeconds())
        .toString());
    body.put("id", userDetails.getUserDto().id());
    body.put("username", userDetails.getUserDto().username());
    body.put("email", userDetails.getUserDto().email());
    body.put("profile", userDetails.getUserDto().profile());
    body.put("online", userDetails.getUserDto().online());
    body.put("role", userDetails.getUserDto().role());

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setHeader("Authorization", "Bearer " + accessToken);
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie(accessToken).toString());
    objectMapper.writeValue(response.getWriter(), body);
  }

  private ResponseCookie refreshTokenCookie(String refreshToken) {
    return ResponseCookie.from("refreshToken", refreshToken)
        .path("/")
        .httpOnly(true)
        .sameSite("Lax")
        .maxAge(jwtTokenProvider.getExpirationSeconds())
        .build();
  }
}

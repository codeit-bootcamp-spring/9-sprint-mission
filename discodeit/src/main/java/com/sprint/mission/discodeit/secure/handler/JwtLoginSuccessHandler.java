package com.sprint.mission.discodeit.secure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.secure.DiscodeitUserDetails;
import com.sprint.mission.discodeit.secure.JwtInformation;
import com.sprint.mission.discodeit.secure.JwtRegistry;
import com.sprint.mission.discodeit.secure.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;
  private final AuthService authService;
  private final JwtRegistry jwtRegistry;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

    String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
    String refreshToken = jwtTokenProvider.generateRefreshToken();

    authService.deleteSession(userDetails.getUserDto().id());
    authService.saveSession(userDetails.getUserDto().id(), accessToken, refreshToken);

    JwtInformation jwtInformation = new JwtInformation(
        userDetails.getUserDto().id(),
        accessToken,
        refreshToken,
        Instant.now().plusSeconds(jwtTokenProvider.getRefreshTokenValiditySeconds())
    );
    jwtRegistry.registerJwtInformation(userDetails.getUserDto().id(), jwtInformation);

    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(
        JwtDto.builder()
            .userDto(userDetails.getUserDto())
            .accessToken(accessToken)
            .build()
    ));

    log.info("User [{}] logged in successfully", userDetails.getUsername());

  }
}

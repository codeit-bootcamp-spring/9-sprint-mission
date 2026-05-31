package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLoginSuccessHandler
    implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

    org.springframework.http.ResponseCookie refreshCookie =
        org.springframework.http.ResponseCookie.from("REFRESH_TOKEN", refreshToken)
            .path("/api/auth")
            .httpOnly(true)
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build();

    response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, refreshCookie.toString());
    UserDto userDto = null;
    if (userDetails instanceof DiscodeitUserDetails customUser) {
      userDto = customUser.getUserDto();
    }

    JwtInformation jwtInfo = new JwtInformation(userDto, accessToken, refreshToken);
    jwtRegistry.registerJwtInformation(jwtInfo);

    JwtDto jwtDto = new JwtDto(userDto, accessToken);


    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
  }
}

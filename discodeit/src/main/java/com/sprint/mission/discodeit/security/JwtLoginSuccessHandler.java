package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
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


  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

    String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

    jakarta.servlet.http.Cookie refreshCookie =
        new jakarta.servlet.http.Cookie(
            "REFRESH_TOKEN",
            refreshToken
        );

    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(7 * 24 * 60 * 60);

    response.addCookie(refreshCookie);

    UserDto userDto = null;
    if (userDetails instanceof DiscodeitUserDetails customUser) {
      userDto = customUser.getUserDto();
    }

    JwtDto jwtDto = new JwtDto(userDto, accessToken);


    response.setCharacterEncoding(
        "UTF-8"
    );

    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
  }
}

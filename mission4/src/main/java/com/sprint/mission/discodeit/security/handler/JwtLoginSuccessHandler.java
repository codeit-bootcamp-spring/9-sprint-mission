package com.sprint.mission.discodeit.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    log.info("[JwtLoginSuccessHandler] 로그인 성공 - JWT 토큰 발급 프로세스 가동");

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    String accessToken = jwtTokenProvider.createAccessToken(authentication);

    String refreshToken = "mock-refresh-token-" + authentication.getName();
    Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(60 * 60 * 24 * 7);
    response.addCookie(refreshCookie);

    Object principal = authentication.getPrincipal();
    UserDto userDto;

    if (principal instanceof DiscodeitUserDetails userDetails) {
      userDto = UserDto.builder()
          .id(userDetails.getId())
          .username(userDetails.getUsername())
          .email(userDetails.getEmail())
          .online(true)
          .build();
    } else {
      throw new IllegalStateException("예상치 못한 인증 객체 타입입니다.");
    }

    JwtDto jwtDto = new JwtDto(accessToken, userDto);

    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
    log.info("[JwtLoginSuccessHandler] Access Token 바디 주입 및 Refresh Token 쿠키 설정 완료");


  }


}

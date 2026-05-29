package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtInformation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    log.info("[JwtLoginSuccessHandler] 로그인 성공 - JWT 토큰 발급 및 장부 등록 프로세스");

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    String accessToken = jwtTokenProvider.createAccessToken(authentication);
    String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
    LocalDateTime accessExpires = jwtTokenProvider.getExpiration(accessToken);
    LocalDateTime refreshExpires = jwtTokenProvider.getExpiration(refreshToken);

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    JwtInformation jwtInfo = new JwtInformation(
        userDetails.getId(),
        accessToken,
        refreshToken,
        accessExpires,
        refreshExpires
    );
    jwtRegistry.registerJwtInformation(jwtInfo);
    log.info("[JwtLoginSuccessHandler] 유저 [{}]의 토큰 장부 등록 완료", userDetails.getId());

    ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .sameSite("Lax")
        .maxAge(60 * 60 * 24 * 7)
        .build();

    response.addHeader("Set-Cookie", refreshCookie.toString());

    UserDto userDto = UserDto.builder()
        .id(userDetails.getId())
        .username(userDetails.getUsername())
        .email(userDetails.getEmail())
        .online(true)
        .build();

    JwtDto jwtDto = new JwtDto(accessToken, userDto);
    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
  }
}
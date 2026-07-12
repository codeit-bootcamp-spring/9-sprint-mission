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

    // 1. 토큰 생성 및 만료 시간 추출
    String accessToken = jwtTokenProvider.createAccessToken(authentication);
    String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
    LocalDateTime accessExpires = jwtTokenProvider.getExpiration(accessToken);
    LocalDateTime refreshExpires = jwtTokenProvider.getExpiration(refreshToken);

    // 2. UserDetails에서 정보 추출
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

    // 3. UserDto 구성 (JwtInformation 생성에 필요)
    UserDto userDto = UserDto.builder()
        .id(userDetails.getId())
        .username(userDetails.getUsername())
        .email(userDetails.getEmail())
        .online(true)
        .build();

    // 4. 수정된 JwtInformation 구조에 맞게 객체 생성
    JwtInformation jwtInfo = new JwtInformation(
        userDto,
        accessToken,
        refreshToken,
        accessExpires,
        refreshExpires
    );

    // 5. Redis 장부 등록
    jwtRegistry.registerJwtInformation(jwtInfo);
    log.info("[JwtLoginSuccessHandler] 유저 [{}]의 토큰 장부 등록 완료", userDto.id());

    // 6. Refresh Token 쿠키 설정
    ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
        .httpOnly(true)
        .secure(false) // 개발 환경 고려, 운영 환경에서는 true 권장
        .path("/")
        .sameSite("Lax")
        .maxAge(60 * 60 * 24 * 7)
        .build();

    response.addHeader("Set-Cookie", refreshCookie.toString());

    // 7. 응답 바디 작성
    JwtDto jwtDto = new JwtDto(accessToken, userDto, refreshToken);
    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
  }
}
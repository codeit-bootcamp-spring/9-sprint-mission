package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.SseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtProvider;
  private final AuthService authService;
  private final UserRepository userRepository;
  private final JwtRegistry jwtRegistry;
  private final SseService sseService;
  private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환용

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    // 1. 인증된 사용자의 식별자(이름/ID) 가져오기
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

    UserDto userDto = userDetails.getUserDto();
    User user = userRepository.findById(userDto.id())
        .orElseThrow(() -> new ServletException("User not found"));

    // 2. JwtProvider를 활용해 토큰 발급
    String accessToken = jwtProvider.generateAccessToken(userDetails);
    String refreshToken = jwtProvider.generateRefreshToken(); // Directly generate instead of service call

    // 3. JwtRegistry에 등록 (동시 로그인 제한 포함)
    jwtRegistry.registerJwtInformation(new JwtInformation(userDto, accessToken, refreshToken));

    // SSE 로그인 성공 이벤트 broadcast (online=true)
    UserDto onlineUserDto = new UserDto(
        userDto.id(),
        userDto.username(),
        userDto.email(),
        userDto.profile(),
        true,
        userDto.role()
    );
    sseService.broadcast("users.updated", onlineUserDto);

    // 4. 리프레시 토큰은 쿠키(REFRESH_TOKEN)에 저장
    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true); // HTTPS 환경인 경우 활성화
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge((int) jwtProvider.getRefreshTokenValiditySeconds());
    response.addCookie(refreshTokenCookie);

    // 5. 엑세스 토큰은 응답 Body에 JwtDto 형태로 포함 (200 OK)
    JwtDto jwtDto = new JwtDto(userDto, accessToken);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // JSON 문자열로 변환하여 응답 바디에 작성
    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
  }
}
package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtInformation;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;


  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    if (cacheManager != null && cacheManager.getCache("allUsers") != null) {
      cacheManager.getCache("allUsers").evict("all");
      log.debug("로그인 성공 처리 - 사용자 목록 캐시(allUsers)가 무효화되었습니다.");
    }

    Object principal = authentication.getPrincipal();
    UserDto userDto = convertToUserDto(principal);

    String accessToken = jwtTokenProvider.createAccessToken(authentication);
    String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
    response.addCookie(refreshTokenCookie);

    JwtInformation jwtInformation = new JwtInformation(userDto, accessToken, refreshToken);
    jwtRegistry.registerJwtInformation(jwtInformation);

    JwtDto jwtDto = new JwtDto(userDto, accessToken);

    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
    response.getWriter().flush();
  }

  private UserDto convertToUserDto(Object principal) {

    if (principal instanceof User user) {

      return new UserDto(UUID.randomUUID(), user.getUsername(), null, null, true, null);
    }
    return new UserDto(UUID.randomUUID(), "unknown", null, null, true, null);
  }
}
package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.sse.SseService;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.mockito.Mockito.mock;

class JwtLoginSuccessHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final JwtTokenProvider jwtTokenProvider =
      new JwtTokenProvider("test-jwt-secret-key-for-mission-10-provider", 1800, 3600);
  private final JwtRegistry jwtRegistry = new InMemoryJwtRegistry(jwtTokenProvider);
  private final CacheManager cacheManager = mock(CacheManager.class);
  private final SseService sseService = mock(SseService.class);
  private final JwtLoginSuccessHandler handler =
      new JwtLoginSuccessHandler(objectMapper, jwtTokenProvider, jwtRegistry, cacheManager,
          sseService);

  @Test
  @DisplayName("인증 성공 시 200 JwtDto와 refresh token cookie로 응답한다")
  void onAuthenticationSuccess_WritesJwtDtoAndRefreshTokenCookie() throws Exception {
    UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true,
        Role.USER);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "$2a$10$password");
    UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
        .authenticated(userDetails, null, userDetails.getAuthorities());
    MockHttpServletResponse response = new MockHttpServletResponse();

    handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

    JwtDto jwtDto = objectMapper.readValue(response.getContentAsString(), JwtDto.class);
    Cookie refreshTokenCookie = response.getCookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME);
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    assertThat(jwtDto.userDto()).isEqualTo(userDto);
    assertThat(jwtTokenProvider.validateToken(jwtDto.accessToken())).isTrue();
    assertThat(refreshTokenCookie).isNotNull();
    assertThat(refreshTokenCookie.isHttpOnly()).isTrue();
    assertThat(refreshTokenCookie.getPath()).isEqualTo("/");
    assertThat(jwtTokenProvider.validateToken(refreshTokenCookie.getValue())).isTrue();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshTokenCookie.getValue()))
        .isTrue();
  }
}

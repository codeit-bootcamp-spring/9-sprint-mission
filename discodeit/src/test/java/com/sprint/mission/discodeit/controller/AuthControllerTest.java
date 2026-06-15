package com.sprint.mission.discodeit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.config.SecurityConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.sse.SseService;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtLoginSuccessHandler.class, JwtLogoutHandler.class,
    JwtTokenProvider.class})
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LoginFailureHandler loginFailureHandler;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private JwtRegistry jwtRegistry;

  @MockitoBean
  private CacheManager cacheManager;

  @MockitoBean
  private SseService sseService;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("CSRF 토큰 발급 성공 테스트")
  void getCsrfToken_Success() throws Exception {
    mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().isNonAuthoritativeInformation());
  }

  @Test
  @DisplayName("Root page request is not blocked by authentication")
  void rootPage_IsPublic() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(result -> assertThat(result.getResponse().getStatus())
            .isNotEqualTo(HttpStatus.UNAUTHORIZED.value()));
  }

  @Test
  @DisplayName("로그인 요청은 Spring Security 로그인 필터에서 처리한다")
  void login_IsHandledByFormLoginFilter() throws Exception {
    mockMvc.perform(post("/api/auth/login")
        .param("username", "unknown")
        .param("password", "Password1!")
        .with(csrf()));

    verify(loginFailureHandler).onAuthenticationFailure(any(), any(), any());
  }

  @Test
  @DisplayName("JSON 로그인 성공 시 JwtDto와 refresh token cookie로 응답한다")
  void login_WithJsonBody_ReturnsJwtDto() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true);
    String encodedPassword = new BCryptPasswordEncoder().encode("Password1!");
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, encodedPassword);
    given(userDetailsService.loadUserByUsername("testuser")).willReturn(userDetails);

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"username":"testuser","password":"Password1!"}
            """)
        .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userDto.id").value(userId.toString()))
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(result -> assertThat(
            result.getResponse().getCookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME))
            .isNotNull());
  }

  @Test
  @DisplayName("유효한 리프레시 토큰으로 액세스 토큰을 재발급하고 리프레시 토큰을 회전한다")
  void refresh_WithValidRefreshToken_ReturnsJwtDtoAndRotatesRefreshToken() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "refreshuser", "refresh@example.com", null, true,
        Role.USER);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userDto);
    given(userService.find(userId)).willReturn(userDto);
    given(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)).willReturn(true);

    mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, refreshToken))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userDto.id").value(userId.toString()))
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(result -> {
          Cookie rotatedCookie = result.getResponse()
              .getCookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME);
          assertThat(rotatedCookie).isNotNull();
          assertThat(rotatedCookie.getValue()).isNotEqualTo(refreshToken);
          assertThat(jwtTokenProvider.validateRefreshToken(rotatedCookie.getValue())).isTrue();
        });
  }

  @Test
  @DisplayName("유효하지 않은 리프레시 토큰이면 401 ErrorResponse로 응답한다")
  void refresh_WithInvalidRefreshToken_ReturnsUnauthorized() throws Exception {
    mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, "invalid-token"))
            .with(csrf()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()));
  }

  @Test
  @DisplayName("리프레시 토큰 쿠키가 없으면 401 ErrorResponse로 응답한다")
  void refresh_WithoutRefreshToken_ReturnsUnauthorized() throws Exception {
    mockMvc.perform(post("/api/auth/refresh")
            .with(csrf()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()));
  }

  @Test
  @DisplayName("User role update succeeds")
  void updateRole_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "manager", "manager@example.com", null, true,
        Role.CHANNEL_MANAGER);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
        new UserDto(UUID.randomUUID(), "admin", "admin@example.com", null, true, Role.ADMIN),
        "$2a$10$password");
    given(userService.updateRole(any())).willReturn(userDto);

    mockMvc.perform(put("/api/auth/role")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"userId":"%s","newRole":"CHANNEL_MANAGER"}
                """.formatted(userId))
            .with(user(userDetails))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.role").value(Role.CHANNEL_MANAGER.name()));
  }

  @Test
  @DisplayName("로그아웃 성공 테스트")
  void logout_SuccessAndDeletesRefreshTokenCookie() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "$2a$10$password");

    mockMvc.perform(post("/api/auth/logout")
            .cookie(new Cookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, "refresh-token"))
            .with(user(userDetails))
            .with(csrf()))
        .andExpect(status().isNoContent())
        .andExpect(result -> {
          Cookie deletedCookie = result.getResponse()
              .getCookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME);
          assertThat(deletedCookie).isNotNull();
          assertThat(deletedCookie.getMaxAge()).isZero();
          assertThat(deletedCookie.getPath()).isEqualTo("/");
          assertThat(deletedCookie.isHttpOnly()).isTrue();
        });
  }
}

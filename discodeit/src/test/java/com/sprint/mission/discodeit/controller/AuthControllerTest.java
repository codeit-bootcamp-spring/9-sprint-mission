package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.config.SecurityConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LoginSuccessHandler loginSuccessHandler;

  @MockitoBean
  private LoginFailureHandler loginFailureHandler;

  @Test
  @DisplayName("CSRF 토큰 발급 성공 테스트")
  void getCsrfToken_Success() throws Exception {
    mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().isNonAuthoritativeInformation());
  }

  @Test
  @DisplayName("로그인 요청은 Spring Security form login에서 처리한다")
  void login_IsHandledByFormLoginFilter() throws Exception {
    mockMvc.perform(post("/api/auth/login")
        .param("username", "unknown")
        .param("password", "Password1!")
        .with(csrf()));

    verify(loginFailureHandler).onAuthenticationFailure(any(), any(), any());
  }

  @Test
  @DisplayName("현재 사용자 조회 성공 테스트")
  void me_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "$2a$10$password");

    mockMvc.perform(get("/api/auth/me").with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.online").value(true));
  }

  @Test
  @DisplayName("현재 사용자 조회 실패 테스트 - 미인증")
  void me_Unauthenticated() throws Exception {
    mockMvc.perform(get("/api/auth/me"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("로그아웃 성공 테스트")
  void logout_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "$2a$10$password");

    mockMvc.perform(post("/api/auth/logout")
            .with(user(userDetails))
            .with(csrf()))
        .andExpect(status().isNoContent());
  }
}

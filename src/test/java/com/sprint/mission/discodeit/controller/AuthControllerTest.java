package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.config.SecurityConfig;
import com.sprint.mission.discodeit.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.handler.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthService authService;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private LoginSuccessHandler loginSuccessHandler;

  @MockitoBean
  private LoginFailureHandler loginFailureHandler;

  @MockitoBean
  private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @MockitoBean
  private CustomAccessDeniedHandler customAccessDeniedHandler;

  @MockitoBean
  private UserRepository userRepository;

  @BeforeEach
  void setUp() throws Exception {
    String encodedPassword = "$2a$10$pzl9VDbn22.xIMT9Cm3Ny.yH9twgpJIYpgvzS0CaDUH2vwuWnnMFa";

    UserDetails userDetails = User.withUsername("admin")
            .password(encodedPassword)
            .roles("ADMIN")
            .build();

    given(userDetailsService.loadUserByUsername(anyString())).willReturn(userDetails);
    doAnswer(invocation -> {
      HttpServletResponse response = invocation.getArgument(1);
      response.setStatus(HttpServletResponse.SC_OK);
      return null;
    }).when(loginSuccessHandler).onAuthenticationSuccess(any(), any(), any());

    doAnswer(invocation -> {
      HttpServletResponse response = invocation.getArgument(1);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }).when(loginFailureHandler).onAuthenticationFailure(any(), any(), any());
  }
  @Test
  @DisplayName("로그인 성공 테스트 - form-data 방식")
  void login_Success() throws Exception {
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", "admin")
            .param("password", "admin1234!")
            .with(csrf()))
            .andExpect(status().isOk());
  }

  @Test
  @DisplayName("로그인 실패 테스트 - 잘못된 파라미터")
  void login_Failure_InvalidRequest() throws Exception {
    // 아이디나 비밀번호 없이 요청을 보낼 때
    mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "")
                    .param("password", "")
                    .with(csrf()))
            .andExpect(status().isUnauthorized());
  }
}
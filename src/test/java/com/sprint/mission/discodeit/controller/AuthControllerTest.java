package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  @Test
  @DisplayName("로그인 성공 테스트")
  void login_Success() throws Exception {
    // Given
    LoginRequest loginRequest = new LoginRequest("testuser", "Password1!");
    UUID userId = UUID.randomUUID();

    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true, Role.USER);
    
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
        userDto,
        "encodedPassword"
    );

    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );

    given(authenticationManager.authenticate(any())).willReturn(authentication);

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.online").value(true));
  }

  @Test
  @DisplayName("로그인 실패 테스트 - 존재하지 않는 사용자")
  void login_Failure_UserNotFound() throws Exception {
    // Given
    LoginRequest loginRequest = new LoginRequest("nonexistentuser", "Password1!");

    given(authenticationManager.authenticate(any()))
        .willThrow(new UsernameNotFoundException("사용자를 찾을 수 없음"));

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("로그인 실패 테스트 - 잘못된 비밀번호")
  void login_Failure_InvalidCredentials() throws Exception {
    // Given
    LoginRequest loginRequest = new LoginRequest("testuser", "WrongPassword1!");

    given(authenticationManager.authenticate(any()))
        .willThrow(new BadCredentialsException("잘못된 비밀번호"));

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("로그인 실패 테스트 - 유효하지 않은 요청")
  void login_Failure_InvalidRequest() throws Exception {
    // Given
    LoginRequest invalidRequest = new LoginRequest("", "");

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }
}

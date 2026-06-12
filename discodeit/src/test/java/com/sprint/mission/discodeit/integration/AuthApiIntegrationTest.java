package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("로그인 API 통합 테스트 - 성공")
  void login_Success() throws Exception {
    // Given
    // 테스트 사용자 생성
    UserCreateRequest userRequest = new UserCreateRequest(
        "loginuser",
        "login@example.com",
        "Password1!"
    );

    userService.create(userRequest, Optional.empty());

    // 로그인 요청
    LoginRequest loginRequest = new LoginRequest(
        "loginuser",
        "Password1!"
    );

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .formFields(MultiValueMap.fromMultiValue(Map.of(
                "username", List.of(loginRequest.username()),
                "password", List.of(loginRequest.password())
            ))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userDto.id", notNullValue()))
        .andExpect(jsonPath("$.userDto.username", is("loginuser")))
        .andExpect(jsonPath("$.userDto.email", is("login@example.com")))
        .andExpect(jsonPath("$.accessToken", notNullValue()));
  }

  @Test
  @DisplayName("로그인 API 통합 테스트 - 실패 (존재하지 않는 사용자)")
  void login_Failure_UserNotFound() throws Exception {
    // Given
    LoginRequest loginRequest = new LoginRequest(
        "nonexistentuser",
        "Password1!"
    );

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .formFields(MultiValueMap.fromMultiValue(Map.of(
                "username", List.of(loginRequest.username()),
                "password", List.of(loginRequest.password())
            ))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("로그인 API 통합 테스트 - 실패 (잘못된 비밀번호)")
  void login_Failure_InvalidCredentials() throws Exception {
    // Given
    // 테스트 사용자 생성
    UserCreateRequest userRequest = new UserCreateRequest(
        "loginuser2",
        "login2@example.com",
        "Password1!"
    );

    userService.create(userRequest, Optional.empty());

    // 잘못된 비밀번호로 로그인 시도
    LoginRequest loginRequest = new LoginRequest(
        "loginuser2",
        "WrongPassword1!"
    );

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .formFields(MultiValueMap.fromMultiValue(Map.of(
                "username", List.of(loginRequest.username()),
                "password", List.of(loginRequest.password())
            ))))
        .andExpect(status().isUnauthorized());
  }
} 
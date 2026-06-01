package com.sprint.mission.discodeit.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class LoginTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @MockitoBean
  private UserDetailsService userDetailsService;

  @Test
  @DisplayName("로그인 성공 테스트")
  void login_Success() throws Exception {
    // Given
    LoginRequest loginRequest = new LoginRequest(
        "testuser",
        "Password1!"
    );

    UUID userId = UUID.randomUUID();
    UserDto loggedInUser = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false,
        Role.USER
    );

    given(userDetailsService.loadUserByUsername(any(String.class)))
        .willReturn(new DiscodeitUserDetails(loggedInUser, passwordEncoder.encode("Password1!")));

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .formFields(MultiValueMap.fromMultiValue(Map.of(
                "username", List.of(loginRequest.username()),
                "password", List.of(loginRequest.password())
            ))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userDto.id").value(userId.toString()))
        .andExpect(jsonPath("$.userDto.username").value("testuser"))
        .andExpect(jsonPath("$.userDto.email").value("test@example.com"))
        .andExpect(jsonPath("$.userDto.online").value(false))
        .andExpect(jsonPath("$.accessToken").exists());
  }

  @Test
  @DisplayName("로그인 실패 테스트 - 존재하지 않는 사용자")
  void login_Failure_UserNotFound() throws Exception {
    // Given

    LoginRequest loginRequest = new LoginRequest(
        "nonexistentuser",
        "Password1!"
    );

    given(userDetailsService.loadUserByUsername(any(String.class)))
        .willThrow(UserNotFoundException.withUsername(loginRequest.username()));

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
  @DisplayName("로그인 실패 테스트 - 잘못된 비밀번호")
  void login_Failure_InvalidCredentials() throws Exception {
    // Given
    LoginRequest loginRequest = new LoginRequest(
        "testuser",
        "WrongPassword1!"
    );
    UUID userId = UUID.randomUUID();
    UserDto loggedInUser = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false,
        Role.USER
    );

    given(userDetailsService.loadUserByUsername(any(String.class)))
        .willReturn(new DiscodeitUserDetails(loggedInUser, passwordEncoder.encode("Password1!")));

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

package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private AuthService authService;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("현재 사용자 정보 조회 - 인증되지 않은 사용자")
  void me_Unauthorized() throws Exception {
    // When & Then
    mockMvc.perform(get("/api/auth/me")
            .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("권한 업데이트 - 성공")
  void updateRole_Success() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);
    UserDto updatedUserDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false,
        Role.ADMIN
    );
    UserDto mockUserDto = new UserDto(userId, "testuser", "test@example.com", null, false,
        Role.USER);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(mockUserDto, "password");

    given(authService.updateRole(any(RoleUpdateRequest.class))).willReturn(updatedUserDto);

    // When & Then
    mockMvc.perform(put("/api/auth/role")
            .with(csrf())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.role").value("ADMIN"));
  }

  @Test
  @DisplayName("권한 업데이트 - 인증되지 않은 사용자")
  void updateRole_Unauthorized() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);

    // When & Then
    mockMvc.perform(put("/api/auth/role")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }


}
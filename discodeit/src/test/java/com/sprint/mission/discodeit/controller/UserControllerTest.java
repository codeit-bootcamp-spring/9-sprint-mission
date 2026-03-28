package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private UserStatusService userStatusService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("create success")
  void create_success() throws Exception {
    UUID userId = UUID.randomUUID();

    UserCreateRequest request = new UserCreateRequest(
        "user1",
        "user1@test.com",
        "password"
    );

    UserDto response = new UserDto(
        userId,
        "user1",
        "user1@test.com",
        null,
        true
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    Mockito.when(userService.create(any(), any()))
        .thenReturn(response);

    mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("user1"));
  }

  @Test
  @DisplayName("create validation fail")
  void create_validation_fail() throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        "",
        "wrong-email",
        "123"
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("findAll success")
  void findAll_success() throws Exception {
    UserDto user = new UserDto(
        UUID.randomUUID(),
        "user1",
        "user1@test.com",
        null,
        true
    );

    Mockito.when(userService.findAll())
        .thenReturn(List.of(user));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("user1"));
  }

  @Test
  @DisplayName("delete success")
  void delete_success() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc.perform(delete("/api/users/{id}", userId))
        .andExpect(status().isNoContent());

    Mockito.verify(userService).delete(userId);
  }

  @Test
  @DisplayName("updateUserStatus success")
  void updateUserStatus_success() throws Exception {
    UUID userId = UUID.randomUUID();

    UserStatusDto response = new UserStatusDto(UUID.randomUUID(), userId, Instant.now());

    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());

    Mockito.when(userStatusService.updateByUserId(eq(userId), any()))
        .thenReturn(response);

    mockMvc.perform(patch("/api/users/{id}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.lastActiveAt").exists());
  }
}
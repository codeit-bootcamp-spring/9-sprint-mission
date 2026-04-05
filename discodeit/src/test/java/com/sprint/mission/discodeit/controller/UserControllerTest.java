package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;

//..
@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private UserStatusService userStatusService;

  @MockBean
  private View error;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void createUser_success() throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        "testUser",
        "test@test.com",
        "password123"
    );

    UserDto response = new UserDto(
        UUID.randomUUID(),
        "testUser",
        "test@test.com",
        null,
        true
    );

    given(userService.create(any(), any()))
        .willReturn(response);

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  void createUser_fail_validation() throws Exception {
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
  void findAll_success() throws Exception {
    List<UserDto> users = List.of(
        new UserDto(UUID.randomUUID(), "user1", "u1@test.com", null, true),
        new UserDto(UUID.randomUUID(), "user2", "u2@test.com", null, false)
    );

    given(userService.findAll()).willReturn(users);

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2));
  }

  @Test
  void findAll_fail() throws Exception {
    given(userService.findAll())
        .willThrow(new RuntimeException("DB error"));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void delete_success() throws Exception {
    UUID userId = UUID.randomUUID();

    willDoNothing().given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  void delete_fail() throws Exception {
    UUID userId = UUID.randomUUID();

    willThrow(new UserNotFoundException(userId))
        .given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isBadRequest());
  }
}
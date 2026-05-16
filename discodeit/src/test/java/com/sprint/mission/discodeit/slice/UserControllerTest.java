package com.sprint.mission.discodeit.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  private UUID userId;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userDto = new UserDto(userId, "testUser", "test@example.com", null, true);
  }

  @Test
  @DisplayName("사용자 생성 성공")
  void create_Success() throws Exception {
    UserCreateRequest request = new UserCreateRequest("test@example.com", "testUser", "password");
    String requestJson = objectMapper.writeValueAsString(request);

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes()
    );

    MockMultipartFile filePart = new MockMultipartFile(
        "profile",
        "test.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image-content".getBytes()
    );

    given(userService.create(any(), any())).willReturn(userDto);

    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .file(filePart)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.email").value("test@example.com"));

    verify(userService).create(any(UserCreateRequest.class), any());
  }

  @Test
  @DisplayName("사용자 생성 실패 - 유효하지 않은 이메일")
  void create_Fail_InvalidEmail() throws Exception {
    // Given
    UserCreateRequest request = new UserCreateRequest("test","testUser", "password");
    MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "", "application/json", objectMapper.writeValueAsString(request).getBytes());

    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void update_Success() throws Exception {
    UserUpdateRequest request = new UserUpdateRequest("newname", "newemail@email.com", "newpassword");
    MockMultipartFile userPart = new MockMultipartFile("userUpdateRequest", "", "application/json", objectMapper.writeValueAsString(request).getBytes());

    given(userService.update(eq(userId), any(), any())).willReturn(userDto);

    // When & Then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userPart)
            .with(csrf())
            .with(req -> { req.setMethod("PATCH"); return req; })) // Multipart PATCH 우회
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("사용자 수정 실패")
  void update_Fail() throws Exception {
    UserUpdateRequest request = new UserUpdateRequest("newname", "newemail@email.com", "newpassword");
    MockMultipartFile userPart = new MockMultipartFile("userUpdateRequest", "", "application/json", objectMapper.writeValueAsString(request).getBytes());

    given(userService.update(eq(userId), any(), any()))
        .willThrow(new UserNotFoundException(userId));

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userPart)
            .with(csrf())
            .with(req -> { req.setMethod("PATCH"); return req; }))
        .andExpect(status().isNotFound());
  }
}

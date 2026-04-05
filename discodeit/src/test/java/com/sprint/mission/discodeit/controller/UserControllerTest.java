package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @Test
  @DisplayName("사용자 생성 - 성공")
  void createUser_Success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("testuser", "test@email.com", "password123");
    UserDto mockResult = new UserDto(UUID.randomUUID(), "testuser", "test@email.com", null, true);

    given(userService.create(any(), any())).willReturn(mockResult);

    MockMultipartFile jsonPart = new MockMultipartFile("userCreateRequest", "", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

    // when & then
    mvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testuser"));
  }

  @Test
  @DisplayName("사용자 생성 - 실패 (유효성 검증)")
  void createUser_Fail_Validation() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("", "invalid-email", "short"); // Invalid request

    MockMultipartFile jsonPart = new MockMultipartFile("userCreateRequest", "", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

    // when & then
    mvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.details.username").exists())
        .andExpect(jsonPath("$.details.email").exists())
        .andExpect(jsonPath("$.details.password").exists());
  }

  @Test
  @DisplayName("사용자 삭제 - 성공")
  void deleteUser_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // when & then
    mvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("사용자 삭제 - 실패 (존재하지 않는 사용자)")
  void deleteUser_Fail_NotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    willThrow(new UserNotFoundException()).given(userService).delete(userId);

    // when & then
    mvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
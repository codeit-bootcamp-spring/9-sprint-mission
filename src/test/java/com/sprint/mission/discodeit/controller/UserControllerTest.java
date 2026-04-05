package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.domain.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @Test
  @DisplayName("전체 유저 조회 - 성공")
  void findAll_success() throws Exception {
    UserDto userDto = new UserDto(UUID.randomUUID(), "홍길동", "test@test.com", null, false);
    given(userService.findAll()).willReturn(List.of(userDto));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].username").value("홍길동"))
        .andExpect(jsonPath("$[0].email").value("test@test.com"));
  }

  @Test
  @DisplayName("전체 유저 조회 - 빈 목록")
  void findAll_empty() throws Exception {
    given(userService.findAll()).willReturn(List.of());

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @DisplayName("유저 생성 성공")
  void create_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "홍길동", "test@test.com", null, false);
    given(userService.create(any(), any())).willReturn(userDto);

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new com.sprint.mission.discodeit.dto.request.UserCreateRequest(
                "홍길동", "test@test.com", "password123"
            )
        )
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("홍길동"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  @DisplayName("유저 생성 실패 - 유효성 검증 실패")
  void create_fail_validation() throws Exception {
    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new com.sprint.mission.discodeit.dto.request.UserCreateRequest(
                "", "", ""  // 빈 값으로 유효성 검증 실패
            )
        )
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void delete_success() throws Exception {
    UUID userId = UUID.randomUUID();
    willDoNothing().given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("유저 삭제 실패 - 유저 없음")
  void delete_fail_notFound() throws Exception {
    UUID userId = UUID.randomUUID();
    willThrow(new UserNotFoundException(userId)).given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
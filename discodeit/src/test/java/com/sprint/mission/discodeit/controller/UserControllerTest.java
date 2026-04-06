package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = JpaConfig.class)
@ActiveProfiles("test")
@DisplayName("UserController 슬라이스 테스트")
class UserControllerTest {

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
    userDto = new UserDto(userId, "tester", "test@sprint.com", null, false);
  }

  @Test
  @DisplayName("사용자 생성 - 성공 (201 Created)")
  void create_User_Success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("tester", "test@sprint.com", "password123");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    given(userService.create(any(UserCreateRequest.class), any(Optional.class)))
        .willReturn(userDto);

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("tester"))
        .andExpect(jsonPath("$.email").value("test@sprint.com"));
  }

  @Test
  @DisplayName("사용자 수정 - 성공 (200 OK)")
  void update_User_Success() throws Exception {
    // given
    UserUpdateRequest updateRequest = new UserUpdateRequest("newTester", "new@sprint.com",
        "newPass");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(updateRequest)
    );

    UserDto updatedDto = new UserDto(userId, "newTester", "new@sprint.com", null, false);
    given(userService.update(eq(userId), any(UserUpdateRequest.class), any(Optional.class)))
        .willReturn(updatedDto);

    // when & then (Patch는 multipart() 사용 시 HttpMethod.PATCH 명시 필요)
    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
            .file(requestPart))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("newTester"));
  }

  @Test
  @DisplayName("사용자 전체 조회 - 성공 (200 OK)")
  void findAll_Users_Success() throws Exception {
    // given
    given(userService.findAll()).willReturn(List.of(userDto));

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(userId.toString()))
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  @DisplayName("사용자 상태 업데이트 - 성공 (200 OK)")
  void updateUserStatus_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UUID userStatusId = UUID.randomUUID();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    UserStatusDto statusDto = new UserStatusDto(userStatusId, userId, Instant.MIN);

    given(userStatusService.updateByUserId(eq(userId), any(UserStatusUpdateRequest.class)))
        .willReturn(statusDto);

    // when & then
    mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastActiveAt").value(Instant.MIN.toString()));
  }

  @Test
  @DisplayName("사용자 삭제 - 성공 (204 No Content)")
  void delete_User_Success() throws Exception {
    // given
    willDoNothing().given(userService).delete(userId);

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("사용자 생성 실패 - 잘못된 데이터 형식 (400 Bad Request)")
  void create_User_Fail_InvalidInput() throws Exception {
    // given (예: 필수값 누락 - @Valid에 의해 차단됨을 가정)
    String invalidJson = "{\"username\": \"\"}";
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, invalidJson.getBytes());

    // when & then
    mockMvc.perform(multipart("/api/users").file(requestPart))
        .andExpect(status().isBadRequest());
  }
}
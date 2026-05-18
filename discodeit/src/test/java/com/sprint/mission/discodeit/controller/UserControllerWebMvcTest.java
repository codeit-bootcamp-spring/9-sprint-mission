package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("POST /api/users 성공: multipart 요청으로 사용자를 생성한다")
  void create_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserResponse response = new UserResponse(userId, "jun", "jun@test.com", null, false);

    when(userService.create(any(), any())).thenReturn(response);

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest("jun", "jun@test.com", "password123"))
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("jun"))
        .andExpect(jsonPath("$.email").value("jun@test.com"));
  }

  @Test
  @DisplayName("POST /api/users 실패: 유효하지 않은 요청이면 400 에러 JSON을 반환한다")
  void create_fail_validation() throws Exception {
    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest("j", "not-email", "123"))
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_400"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.details.errors").isArray());
  }

  @Test
  @DisplayName("POST /api/users 실패: 중복 사용자면 409 에러 JSON을 반환한다")
  void create_fail_duplicateUser() throws Exception {
    when(userService.create(any(), any()))
        .thenThrow(new UserAlreadyExistException(Map.of("email", "jun@test.com")));

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest("jun", "jun@test.com", "password123"))
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER_409"))
        .andExpect(jsonPath("$.status").value(409));
  }
}

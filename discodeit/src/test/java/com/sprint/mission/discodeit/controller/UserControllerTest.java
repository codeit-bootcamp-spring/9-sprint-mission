package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
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
  @DisplayName("사용자 등록 성공 테스트 (201 Created)")
  void register_Success() throws Exception {

    UserCreateRequest request = new UserCreateRequest("minju", "test@test.com", "password123");
    UserDto response = new UserDto(UUID.randomUUID(), "minju", "test@test.com", null, true);

    MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest", "",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

    given(userService.create(any(), any())).willReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
            .file(requestPart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("minju"));
  }

  @Test
  @DisplayName("유효하지 않은 이메일 형식으로 가입 요청 시 400 에러 반환")
  void register_Fail_InvalidEmail() throws Exception {

    UserCreateRequest request = new UserCreateRequest("minju", "invalid-email", "password123");
    MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest", "",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
            .file(requestPart))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("V001"));
  }
}
package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
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
  @DisplayName("유저 생성 성공")
  void createUserSuccess() throws Exception {
    UserCreateRequest request = new UserCreateRequest("tester", "test@test.com", "password123");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );
    UserDto response = new UserDto(UUID.randomUUID(), "tester", "test@test.com", null, null);

    given(userService.create(any(), any())).willReturn(response);

    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("tester"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  @DisplayName("유저 조회 성공")
  void findAll_Success() throws Exception {
    UserDto user1 = new UserDto(UUID.randomUUID(), "user1", "u1@test.com", null, null);
    UserDto user2 = new UserDto(UUID.randomUUID(), "user2", "u2@test.com", null, null);
    given(userService.findAll()).willReturn(List.of(user1, user2));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].username").value("user1"));
  }

  @Test
  @DisplayName("유저 생성 실패")
  void createUser_Fail_BadRequest() throws Exception {
    UserCreateRequest invalidRequest = new UserCreateRequest("", "", "");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalidRequest)
    );
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }
}
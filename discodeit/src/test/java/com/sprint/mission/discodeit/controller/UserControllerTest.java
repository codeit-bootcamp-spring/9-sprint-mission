package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserService userService;

  @Test
  @DisplayName("유저 생성 성공 (HTTP 201 반환)")
  void create_Success() throws Exception {

    String requestJson = "{\"username\":\"tester\", \"email\":\"test@test.com\", \"password\":\"pass123\"}";

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes()
    );
    MockMultipartFile profile = new MockMultipartFile(
        "profile", "profile.png", MediaType.IMAGE_PNG_VALUE, "dummy image".getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest)
            .file(profile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("유저 생성 실패 - 파라미터 유효성 검사 실패 (HTTP 400 반환)")
  void create_Fail_Validation() throws Exception {
    String invalidJson = "{}";
    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, invalidJson.getBytes()
    );

    MockMultipartFile profile = new MockMultipartFile(
        "profile", "profile.png", MediaType.IMAGE_PNG_VALUE, "dummy image".getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest)
            .file(profile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("전체 유저 조회 성공 (JSON 배열 응답 검증)")
  void findAll_Success() throws Exception {
    given(userService.findAll()).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/users"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("유저 삭제 성공 (HTTP 204 반환)")
  void delete_Success() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andDo(print())
        .andExpect(status().isNoContent());
  }
}
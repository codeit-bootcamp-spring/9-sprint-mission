package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;



  @Test
  @DisplayName("사용자 생성 성공")
  void create_success() throws Exception {

    UserCreateRequest request = new UserCreateRequest(
        "user1",
        "user1@test.com",
        "password123"
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
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value("user1"))
        .andExpect(jsonPath("$.email").value("user1@test.com"));
  }

  @Test
  @DisplayName("사용자 생성 실패 - validation")
  void create_fail_validation() throws Exception {

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
  @DisplayName("사용자 목록 조회")
  void findAll_success() throws Exception {

    UserCreateRequest request = new UserCreateRequest(
        "user2",
        "user2@test.com",
        "password123"
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/users")
        .file(jsonPart)
        .contentType(MediaType.MULTIPART_FORM_DATA));


    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("user2"));
  }


  @Test
  @DisplayName("사용자 삭제 성공")
  void delete_success() throws Exception {

    UserCreateRequest request = new UserCreateRequest(
        "user3",
        "user3@test.com",
        "password123"
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    String response = mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String userId = objectMapper.readTree(response).get("id").asText();

    mockMvc.perform(delete("/api/users/{id}", userId))
        .andExpect(status().isNoContent());
  }


  @Test
  @DisplayName("사용자 상태 업데이트 성공")
  void updateUserStatus_success() throws Exception {

    UserCreateRequest request = new UserCreateRequest(
        "user4",
        "user4@test.com",
        "password123"
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    String response = mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String userId = objectMapper.readTree(response).get("id").asText();

    UserStatusUpdateRequest statusRequest =
        new UserStatusUpdateRequest(Instant.now());

    mockMvc.perform(patch("/api/users/{id}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(statusRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.lastActiveAt").exists());
  }
}
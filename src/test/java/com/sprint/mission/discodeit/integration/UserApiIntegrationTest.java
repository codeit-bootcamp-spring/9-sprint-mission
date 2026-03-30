package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import java.nio.charset.StandardCharsets;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("사용자 API 통합: 생성 후 목록 조회 성공")
  void user_create_and_findAll_success() throws Exception {
    createUser("alice", "alice@test.com", "password123");

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].username", hasItem("alice")));
  }

  @Test
  @DisplayName("사용자 API 통합: 수정 후 삭제 성공")
  void user_update_and_delete_success() throws Exception {
    UUID userId = createUser("bob", "bob@test.com", "password123");

    UserUpdateRequest updateRequest = new UserUpdateRequest("bobby", "bobby@test.com", "newpassword123");
    MockMultipartFile updatePart = new MockMultipartFile(
        "userUpdateRequest",
        "userUpdateRequest",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(updateRequest).getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(updatePart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("bobby"))
        .andExpect(jsonPath("$.email").value("bobby@test.com"));

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].username", not(hasItem("bobby"))));
  }

  @Test
  @DisplayName("사용자 API 통합: 중복 이메일 생성 실패")
  void user_create_fail_duplicate_email() throws Exception {
    createUser("charlie", "dup@test.com", "password123");

    UserCreateRequest duplicate = new UserCreateRequest("charlie2", "dup@test.com", "password123");
    MockMultipartFile duplicatePart = new MockMultipartFile(
        "userCreateRequest",
        "userCreateRequest",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(duplicate).getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(multipart("/api/users")
            .file(duplicatePart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("USER_002"));
  }

  private UUID createUser(String username, String email, String password) throws Exception {
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "userCreateRequest",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    return UUID.fromString(body.get("id").asText());
  }
}


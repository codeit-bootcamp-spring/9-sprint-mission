package com.sprint.mission.discodeit.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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
class UserApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @Transactional
  @DisplayName("POST /api/users + GET /api/users 성공: 사용자를 생성하고 목록에서 조회한다")
  void createAndFindAll_success() throws Exception {
    createUser("jun", "jun@test.com", "password123");

    mockMvc.perform(get("/api/users")
            .with(user("user").roles("USER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].username").value("jun"))
        .andExpect(jsonPath("$.content[0].email").value("jun@test.com"));
  }

  @Test
  @Transactional
  @DisplayName("PATCH /api/users/{id} 성공: multipart 요청으로 사용자 정보를 수정한다")
  void update_success() throws Exception {
    UUID userId = createUser("jun", "jun@test.com", "password123");

    MockMultipartFile userUpdateRequest = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserUpdateRequest
            ("juno", "juno@test.com", "password456"))
    );

    mockMvc.perform(
            multipart("/api/users/{userId}", userId)
                .file(userUpdateRequest)
                .with(user("user").roles("USER"))
                .with(csrf())
                .with(request -> {
                  request.setMethod("PATCH");
                  return request;
                })
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("juno"))
        .andExpect(jsonPath("$.email").value("juno@test.com"));
  }

  @Test
  @Transactional
  @DisplayName("DELETE /api/users/{id} 성공: 사용자를 삭제하면 목록에서 사라진다")
  void delete_success() throws Exception {
    UUID userId = createUser("jun", "jun@test.com", "password123");

    mockMvc.perform(delete("/api/users/{userId}", userId)
            .with(user("user").roles("USER"))
            .with(csrf()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/users")
            .with(user("user").roles("USER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }

  @Test
  @Transactional
  @DisplayName("POST /api/users 실패: 중복 이메일로 생성하면 409를 반환한다")
  void create_fail_duplicateEmail() throws Exception {
    createUser("jun", "jun@test.com", "password123");

    MockMultipartFile duplicateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest
            ("jun2", "jun@test.com", "password123"))
    );

    mockMvc.perform(multipart("/api/users")
            .file(duplicateRequest)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER_409"))
        .andExpect(jsonPath("$.status").value(409));
  }

  private UUID createUser(String username, String email, String password) throws Exception {
    MockMultipartFile createPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest(username, email, password))
    );

    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(createPart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    return UUID.fromString(body.get("id").asText());
  }
}




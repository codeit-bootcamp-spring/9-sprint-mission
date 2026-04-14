package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserIntegrationTest extends IntegrationTestSupport {

  @Test
  @DisplayName("사용자 생성 및 전체 목록 조회 통합 테스트 - 성공")
  void createAndFindAllUser() throws Exception {
    UserCreateRequest request = new UserCreateRequest("tester1", "test1@gmail.com", "pass123");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    mockMvc.perform(multipart("/api/users").file(requestPart))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].username").value("tester1"));
  }

  @Test
  @DisplayName("사용자 중복 생성 시도 - 실패 (400 Bad Request)")
  void createDuplicateUser_Fail() throws Exception {
    // 1. 첫 번째 사용자 생성
    UserCreateRequest request = new UserCreateRequest("tester1", "test1@gmail.com", "pass123");
    MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest", "",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));
    mockMvc.perform(multipart("/api/users").file(requestPart)).andExpect(status().isCreated());

    // 2. 동일한 정보로 다시 생성 시도 (이메일 중복)
    mockMvc.perform(multipart("/api/users").file(requestPart))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 수정 및 삭제 통합 테스트 - 성공")
  void updateAndDeleteUser() throws Exception {
    // 1. 사용자 생성
    UserCreateRequest request = new UserCreateRequest("tester2", "test2@gmail.com", "pass");
    MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest", "",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

    String response = mockMvc.perform(multipart("/api/users").file(requestPart))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    UUID userId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    // 2. 사용자 수정 (Multipart PATCH)
    UserUpdateRequest updateReq = new UserUpdateRequest("updatedTester", "updated@gmail.com",
        "newPass");
    MockMultipartFile updatePart = new MockMultipartFile("userUpdateRequest", "",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updateReq));

    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId).file(updatePart))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updatedTester"));

    // 3. 사용자 삭제
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }
}
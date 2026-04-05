package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // application-test.yaml (H2 설정) 적용
@Transactional // 각 테스트 종료 후 DB 롤백 (독립성 보장)
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("사용자 생성 및 목록 조회 - 통합 성공")
  void createUserAndFindAll_Success() throws Exception {
    // Given
    UserCreateRequest request = new UserCreateRequest("integration_tester", "test@test.com", "password");

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    // When & Then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("integration_tester"));

    // When & Then
    mockMvc.perform(get("/api/users")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].username").value("integration_tester"));
  }

  @Test
  @DisplayName("사용자 삭제 - 실패 (존재하지 않는 ID)")
  void deleteUser_Fail() throws Exception {
    // Given
    UUID fakeId = UUID.randomUUID();

    // When & Then
    mockMvc.perform(delete("/api/users/{userId}", fakeId))
        .andExpect(status().isNotFound());
  }
}
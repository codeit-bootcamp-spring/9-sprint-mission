package com.sprint.mission.discodeit.integrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;

  @Test
  @DisplayName("유저 생성 통합 테스트 - 성공 (실제 DB 저장 확인)")
  void createUser_Integration_Success() throws Exception {
    String requestJson = "{\"username\":\"integrationUser\", \"email\":\"integ@test.com\", \"password\":\"pass123\"}";
    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("integrationUser"));

    boolean exists = userRepository.existsByEmail("integ@test.com");
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("유저 생성 통합 테스트 - 실패 (유효성 검사, 400 반환)")
  void createUser_Integration_Fail() throws Exception {
    String invalidJson = "{\"username\":\"tester\", \"email\":\"test@test.com\", \"password\":\"123\"}";
    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, invalidJson.getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("유저 목록 조회 통합 테스트 - 성공")
  void findAllUsers_Integration_Success() throws Exception {
    User user = new User("findUser", "find@test.com", "pw123", null);
    userRepository.save(user);

    mockMvc.perform(get("/api/users"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("findUser"));
  }

  @Test
  @DisplayName("유저 삭제 통합 테스트 - 성공")
  void deleteUser_Integration_Success() throws Exception {
    User user = new User("deleteTarget", "del@test.com", "pw123", null);
    User savedUser = userRepository.save(user);

    mockMvc.perform(delete("/api/users/{userId}", savedUser.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());

    assertThat(userRepository.findById(savedUser.getId())).isEmpty();
  }
}
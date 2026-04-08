package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("사용자 가입 통합 테스트")
  void create_user_test() throws Exception {
    // When & Then
    mockMvc.perform(multipart("/api/users")
            .file("username", "integration_user".getBytes())
            .file("email", "integration@test.com".getBytes())
            .file("password", "pass123".getBytes())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());

    // DB에 들어갔는지 체크
    assertThat(userRepository.existsByUsername("integration_user")).isTrue();
  }

  @Test
  @DisplayName("사용자 목록 조회 통합 테스트")
  void get_all_users_test() throws Exception {
    // Given
    mockMvc.perform(multipart("/api/users")
        .file("username", "list_user".getBytes())
        .file("email", "list@test.com".getBytes())
        .file("password", "pass123".getBytes())
        .contentType(MediaType.MULTIPART_FORM_DATA));

    // When & Then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @DisplayName("사용자 삭제 통합 테스트")
  void delete_user_test() throws Exception {
    // Given
    // 삭제할 유저도 가입 API를 통해 정상적으로 생성합니다.
    mockMvc.perform(multipart("/api/users")
        .file("username", "del_user".getBytes())
        .file("email", "del@test.com".getBytes())
        .file("password", "pass123".getBytes())
        .contentType(MediaType.MULTIPART_FORM_DATA));

    // 생성된 유저의 ID를 찾아옵니다
    User user = userRepository.findByUsername("del_user").orElseThrow();

    // When & Then
    mockMvc.perform(delete("/api/users/" + user.getId()))
        .andExpect(status().isNoContent()); // 204 확인

    // 진짜 DB에서 지워졌는지 확인
    assertThat(userRepository.findById(user.getId())).isEmpty();
  }
}
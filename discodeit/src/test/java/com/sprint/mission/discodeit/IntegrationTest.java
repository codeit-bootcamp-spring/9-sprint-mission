package com.sprint.mission.discodeit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("사용자 생성부터 채널 생성까지의 통합 흐름 테스트")
  void userAndChannelIntegrationTest() throws Exception {
    User user = userRepository.save(new User("itUser", "it@test.com", "password", null));

    PublicChannelCreateRequest request = new PublicChannelCreateRequest("IT Channel", "Desc");

    mockMvc.perform(post("/api/channels/public")
            .sessionAttr("USER_ID", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("IT Channel"));
  }

  @Test
  @DisplayName("채널 목록 조회 통합 테스트 (200 OK)")
  void findAllChannels_Integration_Success() throws Exception {
    User user = userRepository.save(new User("viewer", "view@test.com", "password", null));

    mockMvc.perform(get("/api/channels")

            .param("userId", user.getId().toString())
            .sessionAttr("USER_ID", user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}
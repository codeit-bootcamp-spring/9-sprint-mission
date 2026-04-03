package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
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
class IntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Test
  @DisplayName("통합 유저 생성 및 전체 목록 조회 성공")
  void user_Integration_Success() throws Exception {
    UserCreateRequest request = new UserCreateRequest("통합유저", "integration@test.com",
        "password123");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/users").file(requestPart))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.username == '통합유저')]").exists())
        .andExpect(jsonPath("$[?(@.email == 'integration@test.com')]").exists());
  }

  @Test
  @DisplayName("통합 공개 채널 생성 및 삭제 성공")
  void channel_Integration_Success() throws Exception {
    PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest("통합채널", "테스트 설명");
    String response = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    String channelId = JsonPath.read(response, "$.id");

    mockMvc.perform(delete("/api/channels/" + channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("통합 메시지 생성 및 채널별 목록 조회 성공")
  void message_Integration_Success() throws Exception {

    User user = userRepository.save(new User("작성자", "author@test.com", "password", null));

    userStatusRepository.save(new UserStatus(user, Instant.now()));

    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "메시지방", "설명"));

    MessageCreateRequest messageRequest = new MessageCreateRequest("통합 테스트 메시지입니다.",
        channel.getId(), user.getId());
    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(messageRequest)
    );

    mockMvc.perform(multipart("/api/messages").file(requestPart))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/messages")
            .param("channelId", channel.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("통합 테스트 메시지입니다."))
        .andExpect(jsonPath("$.content[0].id").exists());
  }
}
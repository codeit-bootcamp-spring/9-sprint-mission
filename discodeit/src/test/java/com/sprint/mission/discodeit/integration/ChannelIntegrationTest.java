package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@EnableJpaAuditing
public class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private ChannelService channelService;



  @Test
  @DisplayName("Private 채널 생성 통합 테스트")
  void createPrivateChannel_WithTwoUsers_Integration() throws Exception {
    User user1 = userRepository.save(new User("user1", "password", "user1@email.com", null));
    User user2 = userRepository.save(new User("user2", "wordpass", "user2@email.com", null));
    userRepository.flush();

    List<UUID> participantIds = List.of(user1.getId(), user2.getId());

    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        participantIds
    );

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(csrf()))
        .andExpect(status().isCreated());

    List<Channel> userChannels = channelRepository.findAllByUserId(user1.getId());

    assertThat(userChannels).isNotEmpty();
  }

  @Test
  @DisplayName("Public 채널 생성 후 수정 성공 테스트")
  void createAndUpdatePublicChannel_Success() throws Exception{
    PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest("test", "test channel");

    String createResponse = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    UUID channelId = UUID.fromString(JsonPath.read(createResponse, "$.id"));

    ChannelUpdateRequest updateRequest = new ChannelUpdateRequest("new name", "new description");

    mockMvc.perform(patch("/api/channels/" + channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("new name"))
        .andExpect(jsonPath("$.description").value("new description"));
  }

  @Test
  @DisplayName("PRIVATE 채널 수정 시도 시 PrivateChannelUpdateException 발생")
  void updatePrivateChannel_Fail() throws Exception {
    User user = userRepository.save(new User("tester", "pw123", "test@test.com", null));

    PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(List.of(user.getId()));
    ChannelDto privateChannel = channelService.createPrivateChannel(createRequest);
    UUID privateChannelId = privateChannel.id();

    ChannelUpdateRequest updateRequest = new ChannelUpdateRequest("new name", "new desc");

    mockMvc.perform(patch("/api/channels/" + privateChannelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isBadRequest());
  }
}
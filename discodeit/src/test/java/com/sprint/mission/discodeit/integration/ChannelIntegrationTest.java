package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("공개 채널 생성 통합 테스트")
  void create_public_channel_test() throws Exception {
    // Given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("통합채널", "설명");

    // When & Then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    // DB에 채널이 생겼는지 확인
    assertThat(channelRepository.findAll()).isNotEmpty();
  }

  @Test
  @DisplayName("채널 삭제 통합 테스트")
  void delete_channel_test() throws Exception {
    // Given
    Channel channel = channelRepository.save(new Channel("삭제할채널", "설명", ChannelType.PUBLIC));

    // When & Then
    mockMvc.perform(delete("/api/channels/" + channel.getId()))
        .andExpect(status().isNoContent());

    // DB에서 확실히 지워졌는지 확인
    assertThat(channelRepository.findById(channel.getId())).isEmpty();
  }
}
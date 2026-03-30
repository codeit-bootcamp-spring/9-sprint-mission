package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.integration.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class ChannelIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("공용채널 API를 호출하면 Db에 공용채널이 저장되어야한다.")
  void createChannel_Success() throws Exception {
    var request = PublicChannelCreateRequest.builder()
        .name("하이").description("갱갱").build();
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
    var createChannel = channelRepository.findByName("하이").orElseThrow();
    assertThat(createChannel.getDescription()).isEqualTo("갱갱");

  }

  @Test
  @DisplayName("채널 삭제 API를 호출하면 Db에서 채널 삭제가되어야한다")
  void deleteChannel_Success() throws Exception {

    Channel channel = channelRepository.save(Channel.builder()
        .name("갱갱").type(ChannelType.PUBLIC).description("hi").build());
    mockMvc.perform(delete("/api/channels/" + channel.getId()))
        .andExpect(status().isNoContent());
    var deleteChannel = channelRepository.findById(channel.getId());
    assertThat(deleteChannel).isEmpty();

  }

}

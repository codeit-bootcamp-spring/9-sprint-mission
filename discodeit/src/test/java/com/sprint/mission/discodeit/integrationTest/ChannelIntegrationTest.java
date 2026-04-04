package com.sprint.mission.discodeit.integrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class ChannelIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ChannelRepository channelRepository;

  @Test
  @DisplayName("퍼블릭 채널 생성 통합 테스트 - 성공 (실제 DB 저장 확인)")
  void createPublicChannel_Integration_Success() throws Exception {
    String requestJson = "{\"name\":\"통합테스트채널\", \"description\":\"통합 테스트용 퍼블릭 채널\"}";

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("통합테스트채널"));

    boolean isSaved = channelRepository.findAll().stream()
        .anyMatch(channel -> channel.getName().equals("통합테스트채널")
            && channel.getType() == ChannelType.PUBLIC);
    assertThat(isSaved).isTrue();
  }

  @Test
  @DisplayName("채널 정보 수정 통합 테스트 - 성공 (실제 DB 변경 확인)")
  void updateChannel_Integration_Success() throws Exception {
    Channel channel = new Channel(ChannelType.PUBLIC, "수정전이름", "수정전설명");
    Channel savedChannel = channelRepository.save(channel);

    String updateJson = "{\"newName\":\"수정채널\", \"newDescription\":\"수정된 설명입니다.\"}";

    mockMvc.perform(patch("/api/channels/{channelId}", savedChannel.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andDo(print())
        .andExpect(status().isOk());

    Channel updatedChannel = channelRepository.findById(savedChannel.getId()).orElseThrow();
    assertThat(updatedChannel.getName()).isEqualTo("수정채널");
  }

  @Test
  @DisplayName("채널 삭제 통합 테스트 - 성공 (DB 삭제 확인)")
  void deleteChannel_Integration_Success() throws Exception {
    Channel channel = new Channel(ChannelType.PRIVATE, "비밀채널", "비밀");
    Channel savedChannel = channelRepository.save(channel);

    mockMvc.perform(delete("/api/channels/{channelId}", savedChannel.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());

    assertThat(channelRepository.findById(savedChannel.getId())).isEmpty();
  }
}

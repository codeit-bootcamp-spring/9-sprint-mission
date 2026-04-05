package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ChannelIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ChannelService channelService;

  @Test
  @DisplayName("통합 테스트: 공개 채널 생성 - 성공")
  void createPublicChannel_Integration_Success() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("Integration Channel", "Description");

    mvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Integration Channel"))
        .andExpect(jsonPath("$.description").value("Description"));
  }

  @Test
  @DisplayName("통합 테스트: 공개 채널 수정 - 성공")
  void updatePublicChannel_Integration_Success() throws Exception {
    ChannelDto savedChannel = channelService.create(new PublicChannelCreateRequest("Old Name", "Old Desc"));
    UUID channelId = savedChannel.id();

    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("New Name", "New Desc");

    mvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Name"))
        .andExpect(jsonPath("$.description").value("New Desc"));
  }

  @Test
  @DisplayName("통합 테스트: 채널 삭제 - 성공")
  void deleteChannel_Integration_Success() throws Exception {
    ChannelDto savedChannel = channelService.create(new PublicChannelCreateRequest("Delete Target", "Desc"));
    UUID channelId = savedChannel.id();

    mvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());

    mvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNotFound());
  }
}
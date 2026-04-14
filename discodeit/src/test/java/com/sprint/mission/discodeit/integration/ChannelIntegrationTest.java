package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ChannelIntegrationTest extends IntegrationTestSupport {

  @Test
  @DisplayName("Public 채널 생성, 수정 및 삭제 통합 테스트 - 성공")
  void channelLifeCycleTest() throws Exception {
    // 1. 채널 생성
    PublicChannelCreateRequest createReq = new PublicChannelCreateRequest("General", "Welcome!");
    String response = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createReq)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("General"))
        .andReturn().getResponse().getContentAsString();

    UUID channelId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

    // 2. 채널 수정
    PublicChannelUpdateRequest updateReq = new PublicChannelUpdateRequest("Notice", "Updated Desc");
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateReq)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Notice"));

    // 3. 채널 삭제
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 시도 - 실패 (404 Not Found)")
  void updateNonExistentChannel_Fail() throws Exception {
    PublicChannelUpdateRequest updateReq = new PublicChannelUpdateRequest("Ghost", "Desc");
    UUID randomChannelId = UUID.randomUUID();

    mockMvc.perform(patch("/api/channels/{channelId}", randomChannelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateReq)))
        .andExpect(status().isNotFound());
  }
}
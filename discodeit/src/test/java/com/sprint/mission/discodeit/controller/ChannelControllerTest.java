package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ChannelService channelService;

  @Test
  @DisplayName("채널 생성 성공 - PUBLIC")
  void create_public_success() throws Exception {
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("channel1", "desc");

    ChannelDto response =
        new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PUBLIC,
            "channel1",
            "desc",
            List.of(),
            Instant.now()
        );

    given(channelService.create(any(PublicChannelCreateRequest.class)))
        .willReturn(response);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("channel1"));
  }

  @Test
  @DisplayName("채널 생성 실패")
  void create_public_fail() throws Exception {
    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("", "");

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널 수정 성공")
  void update_success() throws Exception {
    UUID channelId = UUID.randomUUID();

    PublicChannelUpdateRequest request =
        new PublicChannelUpdateRequest("newName", "newDesc");

    ChannelDto response =
        new ChannelDto(
            channelId,
            ChannelType.PUBLIC,
            "newName",
            "newDesc",
            List.of(),
            Instant.now()
        );

    given(channelService.update(any(), any(PublicChannelUpdateRequest.class)))
        .willReturn(response);

    mockMvc.perform(patch("/api/channels/{id}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("newName"));
  }

  @Test
  @DisplayName("채널 수정 실패")
  void update_fail() throws Exception {
    UUID channelId = UUID.randomUUID();

    PublicChannelUpdateRequest request =
        new PublicChannelUpdateRequest("newName", "newDesc");

    given(channelService.update(any(), any()))
        .willThrow(new RuntimeException());

    mockMvc.perform(patch("/api/channels/{id}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() throws Exception {
    UUID channelId = UUID.randomUUID();

    willDoNothing().given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{id}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 삭제 실패")
  void delete_fail() throws Exception {
    UUID channelId = UUID.randomUUID();

    willThrow(new RuntimeException()).given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{id}", channelId))
        .andExpect(status().isInternalServerError());
  }
}
package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.domain.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @Test
  @DisplayName("PUBLIC 채널 생성 성공")
  void createPublic_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "채널명", "설명", List.of(), null);
    given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(channelDto);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("채널명", "설명")
            )))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("채널명"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("PUBLIC 채널 생성 실패 - 유효성 검증 실패")
  void createPublic_fail_validation() throws Exception {
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("", "설명")  // 빈 채널명
            )))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  @DisplayName("채널 수정 성공")
  void update_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "새채널명", "새설명", List.of(), null);
    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willReturn(channelDto);

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("새채널명", "새설명")
            )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("새채널명"));
  }

  @Test
  @DisplayName("채널 수정 실패 - 채널 없음")
  void update_fail_notFound() throws Exception {
    UUID channelId = UUID.randomUUID();
    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willThrow(new ChannelNotFoundException(channelId));

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("새채널명", "새설명")
            )))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    willDoNothing().given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 삭제 실패 - 채널 없음")
  void delete_fail_notFound() throws Exception {
    UUID channelId = UUID.randomUUID();
    willThrow(new ChannelNotFoundException(channelId)).given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
  }

  @Test
  @DisplayName("유저의 채널 목록 조회 성공")
  void findAll_success() throws Exception {
    UUID userId = UUID.randomUUID();
    ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "채널명", "설명", List.of(), null);
    given(channelService.findAllByUserId(userId)).willReturn(List.of(channelDto));

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].name").value("채널명"));
  }
}
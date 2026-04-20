package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 API 성공 테스트")
  void create_public_channel_success() throws Exception {
    // Given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개방", "설명");

    Channel mockChannel = org.mockito.Mockito.mock(Channel.class);
    given(mockChannel.getName()).willReturn("공개방");

    given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(mockChannel);

    // When & Then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("공개방"));
  }

  @Test
  @DisplayName("채널 목록 조회 API 성공 테스트 - 필수 파라미터 포함")
  void find_all_channels_success() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    // PageResponse는 결과가 없어도 객체는 존재해야 하므로 mock으로 만듭니다.
    given(channelService.findAll(any(UUID.class), any(Integer.class))).willReturn(null);

    // When & Then
    // 파라미터인 userId를 .param()으로 넣어줍니다.
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString())
            .param("page", "0"))
        .andExpect(status().isOk());
  }


  // 멘토님 피드백 반영: 채널 생성 실패 케이스 테스트 추가
  @Test
  @DisplayName("공개 채널 생성 API 실패 테스트 - 이름이 비어있을 때")
  void create_public_channel_fail_when_name_is_empty() throws Exception {
    // Given
    // 채널 이름을 일부러 빈칸("")으로 줍니다.
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "이름이 없는 방");

    // When & Then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
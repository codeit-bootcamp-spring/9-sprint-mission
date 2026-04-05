package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ChannelService channelService;

  @Test
  @DisplayName("사용자 소속 채널 목록 조회 - 성공")
  void findAll_Success() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    ChannelDto mockChannel = new ChannelDto(
        channelId,
        ChannelType.PUBLIC,
        "스프링 스터디",
        "백엔드 화이팅",
        List.of(),
        Instant.now()
    );

    given(channelService.findAllByUserId(userId)).willReturn(List.of(mockChannel));

    // When & Then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("스프링 스터디"))
        .andExpect(jsonPath("$[0].type").value("PUBLIC"));
  }

  @Test
  @DisplayName("채널 삭제 - 실패 (존재하지 않는 채널)")
  void deleteChannel_Fail_NotFound() throws Exception {
    // Given
    UUID fakeId = UUID.randomUUID();
    willThrow(new IllegalArgumentException("채널을 찾을 수 없습니다."))
        .given(channelService).delete(fakeId);

    // When & Then
    mockMvc.perform(delete("/api/channels/{channelId}", fakeId))
        .andExpect(status().isBadRequest());
  }
}
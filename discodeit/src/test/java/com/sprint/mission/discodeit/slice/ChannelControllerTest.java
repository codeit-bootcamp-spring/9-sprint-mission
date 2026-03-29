package com.sprint.mission.discodeit.slice;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.type.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
public class ChannelControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ChannelService channelService;

  private UUID channelId;
  private UUID userId;

  @BeforeEach
  void setUp() {
    channelId = UUID.randomUUID();
    userId = UUID.randomUUID();
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_Success() throws Exception {
    willDoNothing().given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId)
            .with(csrf()))
        .andExpect(status().isNoContent());

    verify(channelService, times(1)).delete(channelId);
  }

  @Test
  @DisplayName("채널 삭제 실패")
  void deleteFail() throws Exception {
    willThrow(new ChannelNotFoundException(channelId))
        .given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId)
            .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 ID로 채널 목록 조회 성공")
  void findByUserId_Success() throws Exception {
    ChannelDto dto1 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "test1", "test11", null, Instant.now());
    ChannelDto dto2 = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "test2", "test22",  List.of(userId), Instant.now());
    List<ChannelDto> channels = List.of(dto1, dto2);

    given(channelService.findAllByUserId(userId)).willReturn(channels);

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2))) // 배열의 크기 검증
        .andExpect(jsonPath("$[0].name", is("test1")))
        .andExpect(jsonPath("$[1].name", is("test2")));
  }

}

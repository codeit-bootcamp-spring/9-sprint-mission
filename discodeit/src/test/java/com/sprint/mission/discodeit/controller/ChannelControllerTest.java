package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@ActiveProfiles("test")
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @Test
  @DisplayName("채널 생설 성공")
  void createPublicChannelSuccess() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개방", "설명");
    ChannelDto response = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "공개방", "설명",
        new ArrayList<>(), Instant.now()
    );
    given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(response);
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("공개방"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("유저 ID 채널 조회 성공")
  void findAllByUserIdSuccess() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    ChannelDto dto = new ChannelDto(
        UUID.randomUUID(), ChannelType.PUBLIC, "조회된채널", "설명", new ArrayList<>(), Instant.now()
    );
    given(channelService.findAllByUserId(userId)).willReturn(List.of(dto));

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("조회된채널"));
  }

  @Test
  @DisplayName("유저 ID 누락")
  void findAllFail() throws Exception {
    mockMvc.perform(get("/api/channels"))
        .andExpect(status().isBadRequest());
  }
}
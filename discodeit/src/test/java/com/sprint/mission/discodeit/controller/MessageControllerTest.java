package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("채널 내 메시지 목록 조회 성공 테스트 (200)")
  void findAllByChannelId_Success() throws Exception {

    UUID channelId = UUID.randomUUID();

    PageResponse<MessageDto> mockResponse = new PageResponse<>(Collections.emptyList(), null, 50,
        false, 0L);

    given(messageService.findAllByChannelId(eq(channelId), any(), anyInt()))
        .willReturn(mockResponse);

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("size", "50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  @DisplayName("필수 파라미터 누락 시 조회 실패 (400)")
  void findAll_Fail_MissingParam() throws Exception {
    mockMvc.perform(get("/api/messages"))
        .andExpect(status().isBadRequest());
  }
}
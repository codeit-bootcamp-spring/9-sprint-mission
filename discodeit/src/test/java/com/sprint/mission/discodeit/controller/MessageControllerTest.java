package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
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

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("채널 메시지 목록 조회 - 성공")
  void findAllByChannelId_Success() throws Exception {
    // Given
    UUID channelId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();

    MessageDto mockMessage = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "안녕하세요! 슬라이스 테스트 중입니다.",
        channelId,
        null,
        List.of()
    );

    PageResponse<MessageDto> mockPageResponse = new PageResponse<>(
        List.of(mockMessage), // content
        null,                 // cursor
        50,                   // size
        false,                // hasNext
        1L                    // totalElements
    );

    given(messageService.findAllByChannelId(eq(channelId), any(), any())).willReturn(mockPageResponse);

    // When & Then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("안녕하세요! 슬라이스 테스트 중입니다."));
  }

  @Test
  @DisplayName("메시지 삭제 - 실패 (존재하지 않는 메시지)")
  void deleteMessage_Fail_NotFound() throws Exception {
    // Given
    UUID fakeId = UUID.randomUUID();
    willThrow(new IllegalArgumentException("메시지를 찾을 수 없습니다."))
        .given(messageService).delete(fakeId);

    // When & Then
    mockMvc.perform(delete("/api/messages/{messageId}", fakeId))
        .andExpect(status().isBadRequest());
  }
}
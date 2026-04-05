package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("메시 생성 - 성공")
  void createMessage_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello World", channelId, authorId);
    MessageDto mockResult = new MessageDto(UUID.randomUUID(), null, null, "Hello World", channelId, null, List.of());
    given(messageService.create(any(), any())).willReturn(mockResult);

    MockMultipartFile jsonPart = new MockMultipartFile("messageCreateRequest", "", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

    // when & then
    mvc.perform(multipart("/api/messages")
            .file(jsonPart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello World"));
  }

  @Test
  @DisplayName("채널 메시지 목록 조회 - 성공")
  void getMessagesByChannel_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    MessageDto message = new MessageDto(UUID.randomUUID(), null, null, "Test Message", channelId, null, List.of());
    PageResponse<MessageDto> mockResponse = new PageResponse<>(List.of(message), null, 1, false, 1L);
    given(messageService.findAllByChannelId(any(), any(), any())).willReturn(mockResponse);

    // when & then
    mvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].content").value("Test Message"));
  }

  @Test
  @DisplayName("채널 메시지 목록 조회 - 실패 (필수 파라미터 누락)")
  void getMessagesByChannel_Fail_MissingParam() throws Exception {
    // when & then
    // 'channelId' is a required parameter
    mvc.perform(get("/api/messages"))
        .andExpect(status().isBadRequest());
  }
}
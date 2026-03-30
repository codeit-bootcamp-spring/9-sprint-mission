package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class MessageControllerWebMvcTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  MessageService messageService;

  @Test
  @DisplayName("PATCH /api/messages/{id} - 메시지 수정 성공")
  void update_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("updated content");
    MessageDto response = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "updated content",
        channelId,
        null,
        List.of()
    );

    given(messageService.update(messageId, request)).willReturn(response);

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("updated content"));
  }

  @Test
  @DisplayName("PATCH /api/messages/{id} - newContent 공백 검증 실패(400)")
  void update_fail_validation() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("");

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_001"))
        .andExpect(jsonPath("$.details.newContent").exists());
  }

  @Test
  @DisplayName("PATCH /api/messages/{id} - 메시지 미존재 실패(404)")
  void update_fail_messageNotFound() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("updated content");

    given(messageService.update(messageId, request)).willThrow(new MessageNotFoundException(messageId));

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("MESSAGE_001"))
        .andExpect(jsonPath("$.details.messageId").value(messageId.toString()));
  }
}


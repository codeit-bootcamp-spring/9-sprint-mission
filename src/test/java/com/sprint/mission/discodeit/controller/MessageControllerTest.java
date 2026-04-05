package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.domain.MessageNotFoundException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UserDto authorDto = new UserDto(authorId, "홍길동", "test@test.com", null, false);
    MessageDto messageDto = new MessageDto(
        UUID.randomUUID(), Instant.now(), null, "안녕하세요", channelId, authorDto, List.of()
    );
    given(messageService.create(any(), any())).willReturn(messageDto);

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new MessageCreateRequest("안녕하세요", channelId, authorId)
        )
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("안녕하세요"));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 유효성 검증 실패")
  void create_fail_validation() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new MessageCreateRequest("", channelId, authorId)  // 빈 content
        )
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UserDto authorDto = new UserDto(UUID.randomUUID(), "홍길동", "test@test.com", null, false);
    MessageDto messageDto = new MessageDto(
        messageId, Instant.now(), Instant.now(), "수정된 내용", channelId, authorDto, List.of()
    );
    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willReturn(messageDto);

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new MessageUpdateRequest("수정된 내용")
            )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("수정된 내용"));
  }

  @Test
  @DisplayName("메시지 수정 실패 - 메시지 없음")
  void update_fail_notFound() throws Exception {
    UUID messageId = UUID.randomUUID();
    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willThrow(new MessageNotFoundException(messageId));

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new MessageUpdateRequest("수정된 내용")
            )))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    willDoNothing().given(messageService).delete(messageId);

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 메시지 없음")
  void delete_fail_notFound() throws Exception {
    UUID messageId = UUID.randomUUID();
    willThrow(new MessageNotFoundException(messageId)).given(messageService).delete(messageId);

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
  }
}
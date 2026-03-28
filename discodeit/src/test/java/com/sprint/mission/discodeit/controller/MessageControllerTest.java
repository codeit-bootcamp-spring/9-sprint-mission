package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private MessageService messageService;

  @DisplayName("메시지 생성 성공")
  @Test
  void create_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest("hello", channelId, userId);

    MessageDto response = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "hello",
        channelId,
        new UserDto(userId, "user", "email@test.com", null, true),
        List.of()
    );

    given(messageService.create(any(), any())).willReturn(response);

    mockMvc.perform(
            multipart("/api/messages")
                .file(new MockMultipartFile(
                    "messageCreateRequest",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(request)
                ))
        )
        .andExpect(status().isCreated());
  }

  @DisplayName("메시지 생성 실패 - 필수값 누락")
  @Test
  void create_fail() throws Exception {
    MessageCreateRequest request = new MessageCreateRequest("", null, null);

    mockMvc.perform(
            multipart("/api/messages")
                .file(new MockMultipartFile(
                    "messageCreateRequest",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(request)
                ))
        )
        .andExpect(status().isBadRequest());
  }

  @DisplayName("메시지 수정 성공")
  @Test
  void update_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("updated");

    MessageDto response = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "updated",
        UUID.randomUUID(),
        null,
        List.of()
    );

    given(messageService.update(messageId, request)).willReturn(response);

    mockMvc.perform(
            patch("/api/messages/" + messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
  }

  @DisplayName("메시지 수정 실패 - 잘못된 요청")
  @Test
  void update_fail() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("");

    mockMvc.perform(
            patch("/api/messages/" + messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest());
  }

  @DisplayName("메시지 삭제 성공")
  @Test
  void delete_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    doNothing().when(messageService).delete(messageId);

    mockMvc.perform(delete("/api/messages/" + messageId))
        .andExpect(status().isNoContent());
  }

  @DisplayName("메시지 조회 성공")
  @Test
  void findAll_success() throws Exception {
    UUID channelId = UUID.randomUUID();

    MessageDto message = new MessageDto(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        "hello",
        channelId,
        null,
        List.of()
    );

    PageResponse<MessageDto> response = new PageResponse<>(
        List.of(message),
        null,
        1,
        false,
        1L
    );

    given(messageService.findAllByChannelId(any(), any(), any())).willReturn(response);

    mockMvc.perform(
            get("/api/messages")
                .param("channelId", channelId.toString())
        )
        .andExpect(status().isOk());
  }

  @DisplayName("메시지 조회 실패 - channelId 없음")
  @Test
  void findAll_fail() throws Exception {
    mockMvc.perform(get("/api/messages"))
        .andExpect(status().isBadRequest());
  }
}
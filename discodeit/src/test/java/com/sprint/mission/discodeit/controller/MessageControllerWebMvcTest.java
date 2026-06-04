package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MessageControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("POST /api/messages 성공: multipart 요청으로 메시지를 생성한다")
  void create_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageResponse response = new MessageResponse(
        messageId,
        Instant.parse("2026-03-27T00:00:00Z"),
        Instant.parse("2026-03-27T00:00:00Z"),
        "hello",
        channelId,
        new UserResponse(authorId, "jun", "jun@test.com", null, true),
        List.of()
    );

    when(messageService.create(any(MessageCreateRequest.class), any())).thenReturn(response);

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new MessageCreateRequest("hello", channelId, authorId))
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("hello"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.username").value("jun"));
  }

  @Test
  @DisplayName("POST /api/messages 실패: 요청이 유효하지 않으면 400 에러 JSON을 반환한다")
  void create_fail_validation() throws Exception {
    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new MessageCreateRequest("", null, null))
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_400"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.details.errors").isArray());
  }

  @Test
  @DisplayName("PATCH /api/messages/{id} 실패: 메시지가 없으면 404 에러 JSON을 반환한다")
  void update_fail_notFound() throws Exception {
    UUID messageId = UUID.randomUUID();

    when(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .thenThrow(new MessageNotFoundException(Map.of("messageId", messageId)));

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MessageUpdateRequest("edit"))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE_404"))
        .andExpect(jsonPath("$.status").value(404));
  }
}

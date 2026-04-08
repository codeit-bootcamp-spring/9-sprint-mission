package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("PATCH /api/messages/{messageId} - 성공: 메시지를 수정한다")
    void update_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(),
            "updated content", UUID.randomUUID(), null, List.of());
        given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
            .willReturn(messageDto);

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new MessageUpdateRequest("updated content"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("updated content"));
    }

    @Test
    @DisplayName("PATCH /api/messages/{messageId} - 실패: 메시지 미존재")
    void update_fail_notFound() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        willThrow(new MessageNotFoundException(Map.of("messageId", messageId)))
            .given(messageService).update(eq(messageId), any(MessageUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new MessageUpdateRequest("content"))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/messages - 성공: 채널 메시지를 조회한다")
    void findAllByChannelId_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PageResponse<MessageDto> pageResponse = new PageResponse<>(
            List.of(), null, 50, false, null);
        given(messageService.findAllByChannelId(eq(channelId), any(), eq(50)))
            .willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("size", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("PATCH /api/messages/{messageId} - 실패: 빈 내용으로 수정 시도 시 400")
    void update_fail_validation() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new MessageUpdateRequest(""))))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/messages - 성공: 메시지를 생성한다")
    void create_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
            "Hello!", channelId, null, List.of());
        given(messageService.create(any(), any())).willReturn(messageDto);

        MessageCreateRequest request = new MessageCreateRequest("Hello!", channelId, authorId);
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));

        // when & then
        mockMvc.perform(multipart("/api/messages").file(requestPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Hello!"));
    }

    @Test
    @DisplayName("DELETE /api/messages/{messageId} - 성공: 메시지를 삭제한다")
    void delete_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        willDoNothing().given(messageService).delete(messageId);

        // when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/messages/{messageId} - 실패: 메시지 미존재")
    void delete_fail_notFound() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        willThrow(new MessageNotFoundException(Map.of("messageId", messageId)))
            .given(messageService).delete(messageId);

        // when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
    }
}

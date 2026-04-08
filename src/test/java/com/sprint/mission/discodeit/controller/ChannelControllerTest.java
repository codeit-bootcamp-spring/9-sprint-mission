package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @Test
    @DisplayName("POST /api/channels/public - 성공: PUBLIC 채널을 생성한다")
    void createPublic_success() throws Exception {
        // given
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "general",
            "일반 채널", List.of(), null);
        given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(channelDto);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PublicChannelCreateRequest("general", "일반 채널"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("general"))
            .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("PATCH /api/channels/{channelId} - 성공: 채널을 수정한다")
    void update_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "new-name",
            "new-desc", List.of(), null);
        given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
            .willReturn(channelDto);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PublicChannelUpdateRequest("new-name", "new-desc"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("new-name"));
    }

    @Test
    @DisplayName("PATCH /api/channels/{channelId} - 실패: PRIVATE 채널 수정 시도")
    void update_fail_privateChannel() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willThrow(new PrivateChannelUpdateException(Map.of("channelId", channelId)))
            .given(channelService).update(eq(channelId), any(PublicChannelUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PublicChannelUpdateRequest("name", "desc"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("PRIVATE_CHANNEL_UPDATE"));
    }

    @Test
    @DisplayName("GET /api/channels - 성공: 사용자의 채널 목록을 조회한다")
    void findAll_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "general",
            null, List.of(), null);
        given(channelService.findAllByUserId(userId)).willReturn(List.of(channelDto));

        // when & then
        mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("general"));
    }

    @Test
    @DisplayName("POST /api/channels/private - 성공: PRIVATE 채널을 생성한다")
    void createPrivate_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null,
            null, List.of(), null);
        given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(channelDto);

        // when & then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PrivateChannelCreateRequest(List.of(userId)))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("DELETE /api/channels/{channelId} - 성공: 채널을 삭제한다")
    void delete_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willDoNothing().given(channelService).delete(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/channels/{channelId} - 실패: 존재하지 않는 채널")
    void delete_fail_notFound() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willThrow(new ChannelNotFoundException(Map.of("channelId", channelId)))
            .given(channelService).delete(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    @Test
    @DisplayName("PUT /api/channels/public - 지원하지 않는 HTTP 메서드 405")
    void methodNotAllowed() throws Exception {
        // when & then
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .put("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("DELETE /api/channels/{channelId} - 서버 오류 발생 시 500")
    void delete_fail_serverError() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willThrow(new RuntimeException("unexpected error"))
            .given(channelService).delete(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }
}

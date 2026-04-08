package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
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

@WebMvcTest(ReadStatusController.class)
class ReadStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReadStatusService readStatusService;

    @Test
    @DisplayName("POST /api/readStatuses - 성공: 읽음 상태를 생성한다")
    void create_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant now = Instant.now();
        ReadStatusDto dto = new ReadStatusDto(UUID.randomUUID(), userId, channelId, now);
        given(readStatusService.create(any(ReadStatusCreateRequest.class))).willReturn(dto);

        // when & then
        mockMvc.perform(post("/api/readStatuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new ReadStatusCreateRequest(userId, channelId, now))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    @DisplayName("PATCH /api/readStatuses/{readStatusId} - 성공: 읽음 상태를 수정한다")
    void update_success() throws Exception {
        // given
        UUID readStatusId = UUID.randomUUID();
        Instant newTime = Instant.now();
        ReadStatusDto dto = new ReadStatusDto(readStatusId, UUID.randomUUID(), UUID.randomUUID(), newTime);
        given(readStatusService.update(eq(readStatusId), any(ReadStatusUpdateRequest.class))).willReturn(dto);

        // when & then
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ReadStatusUpdateRequest(newTime))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(readStatusId.toString()));
    }

    @Test
    @DisplayName("PATCH /api/readStatuses/{readStatusId} - 실패: 읽음 상태 미존재")
    void update_fail_notFound() throws Exception {
        // given
        UUID readStatusId = UUID.randomUUID();
        willThrow(new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId)))
            .given(readStatusService).update(eq(readStatusId), any(ReadStatusUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ReadStatusUpdateRequest(Instant.now()))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("READ_STATUS_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/readStatuses - 성공: 사용자별 읽음 상태를 조회한다")
    void findAllByUserId_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        ReadStatusDto dto = new ReadStatusDto(UUID.randomUUID(), userId, UUID.randomUUID(), Instant.now());
        given(readStatusService.findAllByUserId(userId)).willReturn(List.of(dto));

        // when & then
        mockMvc.perform(get("/api/readStatuses").param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }
}

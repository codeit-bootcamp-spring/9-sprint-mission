package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReadStatusIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID channelId;

    @BeforeEach
    void setUp() throws Exception {
        // 사용자 생성
        UserCreateRequest userRequest = new UserCreateRequest("testuser", "test@email.com", "password1234");
        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(userRequest));
        MvcResult userResult = mockMvc.perform(multipart("/api/users").file(userPart))
            .andExpect(status().isCreated())
            .andReturn();
        userId = UUID.fromString(
            objectMapper.readTree(userResult.getResponse().getContentAsString()).get("id").asText());

        // PRIVATE 채널 생성 (ReadStatus 자동 생성)
        MvcResult channelResult = mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PrivateChannelCreateRequest(List.of(userId)))))
            .andExpect(status().isCreated())
            .andReturn();
        channelId = UUID.fromString(
            objectMapper.readTree(channelResult.getResponse().getContentAsString()).get("id").asText());
    }

    @Test
    @DisplayName("ReadStatus 생성 → 조회 → 수정 통합 테스트")
    void readStatus_lifecycle() throws Exception {
        // 사용자별 읽음 상태 조회 (PRIVATE 채널 생성 시 자동 생성됨)
        MvcResult findResult = mockMvc.perform(get("/api/readStatuses")
                .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].userId").value(userId.toString()))
            .andReturn();

        String readStatusId = objectMapper.readTree(findResult.getResponse().getContentAsString())
            .get(0).get("id").asText();

        // 수정
        Instant newTime = Instant.now();
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ReadStatusUpdateRequest(newTime))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(readStatusId));
    }

    @Test
    @DisplayName("새 ReadStatus 생성 통합 테스트")
    void createReadStatus() throws Exception {
        // 새로운 PUBLIC 채널 생성
        MvcResult chResult = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest("public-ch", null))))
            .andExpect(status().isCreated())
            .andReturn();
        UUID publicChannelId = UUID.fromString(
            objectMapper.readTree(chResult.getResponse().getContentAsString()).get("id").asText());

        // ReadStatus 생성
        mockMvc.perform(post("/api/readStatuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new ReadStatusCreateRequest(userId, publicChannelId, Instant.now()))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(userId.toString()))
            .andExpect(jsonPath("$.channelId").value(publicChannelId.toString()));
    }
}

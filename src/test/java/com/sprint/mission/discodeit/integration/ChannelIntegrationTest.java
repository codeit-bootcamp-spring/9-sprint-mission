package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import java.util.List;
import java.util.UUID;
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
class ChannelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createUser(String username, String email) throws Exception {
        UserCreateRequest request = new UserCreateRequest(username, email, "password1234");
        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));

        MvcResult result = mockMvc.perform(multipart("/api/users").file(requestPart))
            .andExpect(status().isCreated())
            .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 → 수정 → 삭제 통합 테스트")
    void publicChannel_lifecycle() throws Exception {
        String userId = createUser("testuser", "test@email.com");

        // 생성
        MvcResult createResult = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PublicChannelCreateRequest("general", "일반 채널"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("general"))
            .andReturn();

        String channelId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("id").asText();

        // 수정
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PublicChannelUpdateRequest("renamed", "수정된 설명"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("renamed"));

        // 삭제
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 후 수정 시도 시 400 응답")
    void privateChannel_updateFail() throws Exception {
        String userId = createUser("testuser", "test@email.com");

        // PRIVATE 채널 생성
        MvcResult createResult = mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PrivateChannelCreateRequest(List.of(UUID.fromString(userId))))))
            .andExpect(status().isCreated())
            .andReturn();

        String channelId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("id").asText();

        // PRIVATE 채널 수정 시도
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PublicChannelUpdateRequest("name", "desc"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("PRIVATE_CHANNEL_UPDATE"));
    }

    @Test
    @DisplayName("GET /api/channels - 사용자의 채널 목록 조회")
    void findAll_success() throws Exception {
        String userId = createUser("testuser", "test@email.com");

        // PUBLIC 채널 생성
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PublicChannelCreateRequest("general", null))))
            .andExpect(status().isCreated());

        // 조회
        mockMvc.perform(get("/api/channels").param("userId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}

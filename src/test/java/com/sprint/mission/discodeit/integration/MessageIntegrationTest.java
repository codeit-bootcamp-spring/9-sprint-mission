package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
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
class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID channelId;

    @BeforeEach
    void setUp() throws Exception {
        // 사용자 생성
        UserCreateRequest userRequest = new UserCreateRequest("testuser", "test@email.com",
            "password1234");
        MockMultipartFile userPart = new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(userRequest));
        MvcResult userResult = mockMvc.perform(multipart("/api/users").file(userPart))
            .andExpect(status().isCreated())
            .andReturn();
        userId = UUID.fromString(
            objectMapper.readTree(userResult.getResponse().getContentAsString()).get("id")
                .asText());

        // 채널 생성
        MvcResult channelResult = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PublicChannelCreateRequest("general", null))))
            .andExpect(status().isCreated())
            .andReturn();
        channelId = UUID.fromString(
            objectMapper.readTree(channelResult.getResponse().getContentAsString()).get("id")
                .asText());
    }

    @Test
    @DisplayName("메시지 생성 → 수정 통합 테스트")
    void createAndUpdate() throws Exception {
        // 생성
        MessageCreateRequest msgRequest = new MessageCreateRequest("Hello!", channelId, userId);
        MockMultipartFile msgPart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(msgRequest));

        MvcResult createResult = mockMvc.perform(
                multipart("/api/messages").file(msgPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Hello!"))
            .andReturn();

        String messageId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("id").asText();

        // 수정
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new MessageUpdateRequest("Updated!"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Updated!"));
    }

    @Test
    @DisplayName("존재하지 않는 채널에 메시지 생성 시 404 응답")
    void create_fail_channelNotFound() throws Exception {
        MessageCreateRequest request = new MessageCreateRequest("Hello!",
            UUID.randomUUID(), userId);
        MockMultipartFile msgPart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));

        mockMvc.perform(multipart("/api/messages").file(msgPart))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/messages - 채널 메시지 목록 조회")
    void findAllByChannelId_success() throws Exception {
        // 메시지 생성
        MessageCreateRequest request = new MessageCreateRequest("test message", channelId, userId);
        MockMultipartFile msgPart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));
        mockMvc.perform(multipart("/api/messages").file(msgPart))
            .andExpect(status().isCreated());

        // 조회
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("size", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].content").value("test message"));
    }

    @Test
    @DisplayName("첨부파일과 함께 메시지를 생성한다")
    void create_withAttachments() throws Exception {
        MessageCreateRequest request = new MessageCreateRequest("with attachment", channelId, userId);
        MockMultipartFile msgPart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));
        MockMultipartFile attachment = new MockMultipartFile(
            "attachments", "doc.txt", "text/plain", "file content".getBytes());

        mockMvc.perform(multipart("/api/messages").file(msgPart).file(attachment))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("with attachment"));
    }

    @Test
    @DisplayName("메시지 삭제 통합 테스트")
    void deleteMessage() throws Exception {
        // 생성
        MessageCreateRequest request = new MessageCreateRequest("to delete", channelId, userId);
        MockMultipartFile msgPart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));
        MvcResult createResult = mockMvc.perform(multipart("/api/messages").file(msgPart))
            .andExpect(status().isCreated())
            .andReturn();
        String messageId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("id").asText();

        // 삭제
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNoContent());
    }
}

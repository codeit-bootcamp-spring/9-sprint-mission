package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String createUser() throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        "user_msg",
        "msg@test.com",
        "password123"
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    String response = mockMvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn()
        .getResponse()
        .getContentAsString();

    return objectMapper.readTree(response).get("id").asText();
  }

  private String createChannel(String userId) throws Exception {

    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(
            List.of(UUID.fromString(userId))
        );

    String response = mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andReturn()
        .getResponse()
        .getContentAsString();

    return objectMapper.readTree(response).get("id").asText();
  }


  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() throws Exception {

    String userId = createUser();
    String channelId = createChannel(userId);

    MessageCreateRequest request =
        new MessageCreateRequest(
            "hello",
            UUID.fromString(channelId),
            UUID.fromString(userId)
        );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/messages")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.content").value("hello"))
        .andExpect(jsonPath("$.channelId").value(channelId));
  }

  @Test
  @DisplayName("메시지 생성 실패 - validation")
  void create_fail() throws Exception {

    MessageCreateRequest request =
        new MessageCreateRequest(
            null,
            UUID.randomUUID(),
            UUID.randomUUID()
        );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/messages")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }


  @Test
  @DisplayName("채널 메시지 조회")
  void findAll_success() throws Exception {

    String userId = createUser();
    String channelId = createChannel(userId);

    MessageCreateRequest request =
        new MessageCreateRequest(
            "hello",
            UUID.fromString(channelId),
            UUID.fromString(userId)
        );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    mockMvc.perform(multipart("/api/messages")
        .file(jsonPart)
        .contentType(MediaType.MULTIPART_FORM_DATA));

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("hello"));
  }


  @Test
  @DisplayName("메시지 수정 성공")
  void update_success() throws Exception {

    String userId = createUser();
    String channelId = createChannel(userId);

    MessageCreateRequest createRequest =
        new MessageCreateRequest(
            "hello",
            UUID.fromString(channelId),
            UUID.fromString(userId)
        );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(createRequest)
    );

    String response = mockMvc.perform(multipart("/api/messages")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String messageId = objectMapper.readTree(response).get("id").asText();

    MessageUpdateRequest updateRequest =
        new MessageUpdateRequest("updated");

    mockMvc.perform(patch("/api/messages/{id}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("updated"));
  }


  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() throws Exception {

    String userId = createUser();
    String channelId = createChannel(userId);

    MessageCreateRequest createRequest =
        new MessageCreateRequest(
            "hello",
            UUID.fromString(channelId),
            UUID.fromString(userId)
        );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(createRequest)
    );

    String response = mockMvc.perform(multipart("/api/messages")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String messageId = objectMapper.readTree(response).get("id").asText();

    mockMvc.perform(delete("/api/messages/{id}", messageId))
        .andExpect(status().isNoContent());
  }
}
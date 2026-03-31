package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
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
class ChannelControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String createUser() throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        "user_channel",
        "channel@test.com",
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


  @Test
  @DisplayName("공개 채널 생성 성공")
  void create_public_success() throws Exception {

    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("채널1", "설명");

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("채널1"));
  }

  @Test
  @DisplayName("공개 채널 생성 실패 - validation")
  void create_public_fail() throws Exception {

    PublicChannelCreateRequest request =
        new PublicChannelCreateRequest("", "설명");

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }


  @Test
  @DisplayName("비공개 채널 생성 성공")
  void create_private_success() throws Exception {

    String userId = createUser();

    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(
            List.of(UUID.fromString(userId))
        );

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.participants[0].id").value(userId));
  }

  @Test
  @DisplayName("비공개 채널 생성 실패 - participant 없음")
  void create_private_fail() throws Exception {

    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(List.of());

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }


  @Test
  @DisplayName("사용자 채널 조회")
  void findAll_success() throws Exception {

    String userId = createUser();

    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(
            List.of(UUID.fromString(userId))
        );

    mockMvc.perform(post("/api/channels/private")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    mockMvc.perform(get("/api/channels")
            .param("userId", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").exists());
  }


  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() throws Exception {

    String userId = createUser();

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

    String channelId = objectMapper.readTree(response).get("id").asText();

    mockMvc.perform(delete("/api/channels/{id}", channelId))
        .andExpect(status().isNoContent());
  }
}
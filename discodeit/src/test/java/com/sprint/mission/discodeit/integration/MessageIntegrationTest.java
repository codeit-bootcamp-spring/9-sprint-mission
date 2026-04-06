package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class MessageIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("메시지 생성, 조회, 수정, 삭제 통합 테스트 - 성공")
  void messageLifeCycleTest() throws Exception {
    // 1. 사전 준비: User 및 Channel 생성
    User author = userRepository.save(new User("writer", "writer@g.com", "pass", null));

    PublicChannelCreateRequest channelReq = new PublicChannelCreateRequest("General", "Desc");
    String channelResponse = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(channelReq)))
        .andReturn().getResponse().getContentAsString();
    UUID channelId = UUID.fromString(objectMapper.readTree(channelResponse).get("id").asText());

    // 2. 메시지 생성
    MessageCreateRequest messageReq = new MessageCreateRequest("Hello World", channelId,
        author.getId());
    MockMultipartFile msgPart = new MockMultipartFile("messageCreateRequest", "",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(messageReq));

    String msgResponse = mockMvc.perform(multipart("/api/messages").file(msgPart))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    UUID messageId = UUID.fromString(objectMapper.readTree(msgResponse).get("id").asText());

    // 3. 메시지 목록 조회
    mockMvc.perform(
            get("/api/messages").param("channelId", channelId.toString()).param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("Hello World"));

    // 4. 메시지 수정
    MessageUpdateRequest updateReq = new MessageUpdateRequest("Updated Message");
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateReq)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated Message"));

    // 5. 메시지 삭제
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 채널에 메시지 작성 시도 - 실패 (404 Not Found)")
  void createMessageInNonExistentChannel_Fail() throws Exception {
    User author = userRepository.save(new User("hacker", "hacker@g.com", "pass", null));
    UUID randomChannelId = UUID.randomUUID();

    MessageCreateRequest messageReq = new MessageCreateRequest("Hello", randomChannelId,
        author.getId());
    MockMultipartFile msgPart = new MockMultipartFile("messageCreateRequest", "",
        MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(messageReq));

    mockMvc.perform(multipart("/api/messages").file(msgPart))
        .andExpect(status().isNotFound()); // 채널이 없으므로 404를 기대함
  }
}
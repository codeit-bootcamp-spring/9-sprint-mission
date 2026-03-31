package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private MessageRepository messageRepository;

  @Test
  @Transactional
  @DisplayName("POST /api/messages 성공: 메시지를 생성한다")
  void create_success() throws Exception {
    User author = userRepository.save(new User("jun", "jun@test.com", "password123", null));
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new MessageCreateRequest("hello", channel.getId(), author.getId()))
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("hello"))
        .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
        .andExpect(jsonPath("$.author.username").value("jun"));
  }

  @Test
  @Transactional
  @DisplayName("PATCH /api/messages/{id} 성공: 메시지 내용을 수정한다")
  void update_success() throws Exception {
    User author = userRepository.save(new User("juno", "juno@test.com", "password123", null));
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    Message message = messageRepository.save(new Message("before", channel, author));

    mockMvc.perform(patch("/api/messages/{messageId}", message.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MessageUpdateRequest("after"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(message.getId().toString()))
        .andExpect(jsonPath("$.content").value("after"));
  }

  @Test
  @Transactional
  @DisplayName("DELETE /api/messages/{id} 성공: 삭제 후 다시 수정하면 404를 반환한다")
  void delete_success() throws Exception {
    User author = userRepository.save(new User("kim", "kim@test.com", "password123", null));
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    Message message = messageRepository.save(new Message("hello", channel, author));

    mockMvc.perform(delete("/api/messages/{messageId}", message.getId()))
        .andExpect(status().isNoContent());

    mockMvc.perform(patch("/api/messages/{messageId}", message.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MessageUpdateRequest("after-delete"))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE_404"));
  }

  @Test
  @Transactional
  @DisplayName("GET /api/messages 성공: 채널 메시지를 페이징으로 조회한다")
  void findAllByChannelId_success() throws Exception {
    User author = userRepository.save(new User("park", "park@test.com", "password123", null));
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    messageRepository.save(new Message("first", channel, author));
    messageRepository.save(new Message("second", channel, author));
    messageRepository.save(new Message("third", channel, author));

    mockMvc.perform(get("/api/messages")
            .param("channelId", channel.getId().toString())
            .param("cursor", Instant.now().toString())
            .param("page", "0")
            .param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @Transactional
  @DisplayName("GET /api/messages 실패: 없는 채널이면 404를 반환한다")
  void findAllByChannelId_fail_notFoundChannel() throws Exception {
    mockMvc.perform(get("/api/messages")
            .param("channelId", UUID.randomUUID().toString())
            .param("cursor", Instant.now().toString())
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_404"))
        .andExpect(jsonPath("$.status").value(404));
  }
}

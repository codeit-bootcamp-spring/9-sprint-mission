package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MessageIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MessageService messageService;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  private UUID testUserId;
  private UUID testChannelId;

  @BeforeEach
  void setUp() {
    UserDto user = userService.create(new UserCreateRequest("msgUser", "msg@email.com", "pass123"), Optional.empty());
    testUserId = user.id();

    ChannelDto channel = channelService.create(new PublicChannelCreateRequest("msgChannel", "desc"));
    testChannelId = channel.id();
  }

  @Test
  @DisplayName("통합 테스트: 메시지 생성 - 성공")
  void createMessage_Integration_Success() throws Exception {
    MessageCreateRequest request = new MessageCreateRequest("Integration Test Message", testChannelId, testUserId);
    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    mvc.perform(multipart("/api/messages")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Integration Test Message"));
  }

  @Test
  @DisplayName("통합 테스트: 채널의 메시지 목록 조회 - 성공")
  void getMessages_Integration_Success() throws Exception {
    messageService.create(new MessageCreateRequest("Msg 1", testChannelId, testUserId), new ArrayList<>());
    messageService.create(new MessageCreateRequest("Msg 2", testChannelId, testUserId), new ArrayList<>());

    mvc.perform(get("/api/messages")
            .param("channelId", testChannelId.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
  }

  @Test
  @DisplayName("통합 테스트: 메시지 수정 - 성공")
  void updateMessage_Integration_Success() throws Exception {
    MessageDto savedMessage = messageService.create(new MessageCreateRequest("Old Content", testChannelId, testUserId), new ArrayList<>());
    UUID messageId = savedMessage.id();

    MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated Content");

    mvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated Content"));
  }

  @Test
  @DisplayName("통합 테스트: 메시지 삭제 - 성공")
  void deleteMessage_Integration_Success() throws Exception {
    MessageDto savedMessage = messageService.create(new MessageCreateRequest("Delete Me", testChannelId, testUserId), new ArrayList<>());
    UUID messageId = savedMessage.id();

    mvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());

    mvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound());
  }
}
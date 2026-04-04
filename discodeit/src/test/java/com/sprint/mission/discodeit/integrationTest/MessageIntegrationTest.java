package com.sprint.mission.discodeit.integrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Collections;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private MessageRepository messageRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ChannelRepository channelRepository;

  private User testUser;
  private Channel testChannel;

  @BeforeEach
  void setUp() {
    testUser = userRepository.save(new User("tester", "test@test.com", "pw123", null));
    testChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "테스트채널", "설명"));
  }

  @Test
  @DisplayName("메시지 생성 통합 테스트 - 성공 (실제 DB 저장 확인)")
  void createMessage_Integration_Success() throws Exception {
    String requestJson = "{\"channelId\":\"" + testChannel.getId() + "\", \"authorId\":\"" + testUser.getId() + "\", \"content\":\"통합 테스트용 진짜 메시지!\"}";
    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes()
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("통합 테스트용 진짜 메시지!"));

    boolean isSaved = messageRepository.findAll().stream()
        .anyMatch(msg -> msg.getContent().equals("통합 테스트용 진짜 메시지!"));
    assertThat(isSaved).isTrue();
  }

  @Test
  @DisplayName("메시지 수정 통합 테스트 - 성공 (실제 DB 변경 확인)")
  void updateMessage_Integration_Success() throws Exception {
    Message message = new Message("수정전 내용", testChannel, testUser, Collections.emptyList());
    Message savedMessage = messageRepository.save(message);

    String updateJson = "{\"newContent\":\"완벽하게 수정된 내용\"}";

    mockMvc.perform(patch("/api/messages/{messageId}", savedMessage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andDo(print())
        .andExpect(status().isOk());

    Message updatedMessage = messageRepository.findById(savedMessage.getId()).orElseThrow();
    assertThat(updatedMessage.getContent()).isEqualTo("완벽하게 수정된 내용");
  }

  @Test
  @DisplayName("채널 내 메시지 조회 통합 테스트 - 성공 (페이징 및 DB 데이터 확인)")
  void findAllMessagesByChannelId_Integration_Success() throws Exception {
    messageRepository.save(new Message("메시지 1", testChannel, testUser, Collections.emptyList()));
    messageRepository.save(new Message("메시지 2", testChannel, testUser, Collections.emptyList()));

    mockMvc.perform(get("/api/messages")
            .param("channelId", testChannel.getId().toString())
            .param("size", "20")
            .param("sort", "createdAt,desc"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  @Test
  @DisplayName("메시지 삭제 통합 테스트 - 성공 (DB 삭제 확인)")
  void deleteMessage_Integration_Success() throws Exception {
    Message message = new Message("폭파될 메시지", testChannel, testUser, Collections.emptyList());
    Message savedMessage = messageRepository.save(message);

    mockMvc.perform(delete("/api/messages/{messageId}", savedMessage.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());

    assertThat(messageRepository.findById(savedMessage.getId())).isEmpty();
  }
}
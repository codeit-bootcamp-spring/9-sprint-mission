package com.sprint.mission.discodeit.integraton;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@EnableJpaAuditing
class MessageIntegrationTest {

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

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  private User testUser;
  private Channel testChannel;

  @BeforeEach
  void setUp() {
    testUser = userRepository.save(new User("user", "password", "test@email.com", null));
    testChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "testch", "test channel"));
  }

  @Test
  @DisplayName("메시지 전송 통합 테스트 - 첨부파일 포함")
  void sendMessage_WithAttachments_Integration() throws Exception {
    MessageCreateRequest request = new MessageCreateRequest("hello", testChannel.getId(), testUser.getId());

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile filePart = new MockMultipartFile(
        "attachments",
        "image.png",
        MediaType.IMAGE_PNG_VALUE,
        "image-content".getBytes()
    );

    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .file(filePart)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("hello"))
        .andExpect(jsonPath("$.author.id").value(testUser.getId().toString()));

    assertThat(messageRepository.findAll()).hasSize(1);
  }

  @Test
  @DisplayName("메시지 목록 조회 - 첨부파일 포함")
  void findMessagesWithAttachments_Integration() throws Exception {
    BinaryContent file1 = binaryContentRepository.save(
        new BinaryContent("image1.png", 1024L, "image/png"));
    BinaryContent file2 = binaryContentRepository.save(
        new BinaryContent("image2.png", 2048L, "image/png"));

    Message messageWithFiles = new Message(
        testChannel,
        testUser,
        "파일이 포함된 메시지입니다.",
        List.of(file1, file2)
    );
    messageRepository.save(messageWithFiles);
    messageRepository.flush();

    mockMvc.perform(get("/api/messages")
            .param("channelId", testChannel.getId().toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("파일이 포함된 메시지입니다."))
        .andExpect(jsonPath("$.content[0].attachments.length()").value(2))
        .andExpect(jsonPath("$.content[0].attachments[0].fileName").value("image1.png"))
        .andExpect(jsonPath("$.content[0].attachments[1].fileName").value("image2.png"))
        .andExpect(jsonPath("$.content[0].author.id").value(testUser.getId().toString()));
  }
}

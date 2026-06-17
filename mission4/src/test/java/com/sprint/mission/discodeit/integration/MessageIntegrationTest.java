package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.integration.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class MessageIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ChannelRepository channelRepository;


  @Test
  @DisplayName("메시지 생성 API를 호출하면 DB에 생성된 메시지가 저장됌")
  void createMessage_success() throws Exception {
    var user = userRepository.save(User.builder()
        .username("메시지작성자").email("msg@test.com").password("1234").build());
    var channel = channelRepository.save(Channel.builder()
        .name("테스트채널").type(ChannelType.PUBLIC).build());
    var request = MessageCreateRequest.builder().content("하이").authorId(user.getId())
        .channelId(channel.getId()).build();
    MockMultipartFile requestFile = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        mapper.writeValueAsBytes(request)
    );

    MockMultipartFile attachmentsFile = new MockMultipartFile(
        "attachments",
        "image/png",
        MediaType.IMAGE_PNG_VALUE,
        new byte[]{1, 2, 3, 4}
    );

    mockMvc.perform(multipart("/api/messages")
            .file(requestFile)
            .file(attachmentsFile))
        .andExpect(status().isCreated());

    var messages = messageRepository.findAll();
    assertThat(messages).isNotEmpty();
    var savedMessage = messages.get(messages.size() - 1);
    assertThat(savedMessage.getContent()).isEqualTo("하이");
    assertThat(savedMessage.getAuthor().getId()).isEqualTo(user.getId());

  }

  @Test
  @DisplayName("메시지 수정 Api를 날리면 DB에 메시지 수정된것이 저장되어야함(프로필x)")
  void updateMessage_Success() throws Exception {
    User user = userRepository.save(
        User.builder().username("승").email("seung@naver.com").password("12315235125")
            .build());
    Channel channel = channelRepository.save(
        Channel.builder().name("채널").type(ChannelType.PUBLIC).description("갱")
            .build());
    Message message = messageRepository.save(
        Message.builder().content("원래 메시지임").author(user).channel(channel).build());
    var request = new MessageUpdateRequest("안녕하세요");
    mockMvc.perform(patch("/api/messages/" + message.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    var savedMessage = messageRepository.findById(message.getId()).orElseThrow();
    assertThat(savedMessage.getId()).isEqualTo(message.getId());
    assertThat(savedMessage.getContent()).isEqualTo("안녕하세요");


  }
}

package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  private User createUser() {
    User user = new User("user1", "user1@test.com", "pw", null);
    User saved = userRepository.save(user);

    UserStatus status = new UserStatus(saved, Instant.now());
    userStatusRepository.save(status);

    return saved;
  }

  private Channel createChannel() {
    return channelRepository.save(
        new Channel(ChannelType.PUBLIC, "channel", "desc")
    );
  }

  private Message createMessage(Channel channel, User user) {
    return messageRepository.save(
        new Message("content", channel, user, List.of())
    );
  }

  @Test
  @DisplayName("findAllByChannelIdWithAuthor success")
  void findAllByChannelIdWithAuthor_success() {
    User user = createUser();
    Channel channel = createChannel();

    createMessage(channel, user);
    createMessage(channel, user);

    Pageable pageable = PageRequest.of(0, 10);

    Slice<Message> result =
        messageRepository.findAllByChannelIdWithAuthor(
            channel.getId(),
            Instant.now().plusSeconds(1),
            pageable
        );

    assertThat(result.getContent().size()).isEqualTo(2);
  }

  @Test
  @DisplayName("findLastMessageAtByChannelId success")
  void findLastMessageAtByChannelId_success() {
    User user = createUser();
    Channel channel = createChannel();

    createMessage(channel, user);
    createMessage(channel, user);

    Optional<Instant> result =
        messageRepository.findLastMessageAtByChannelId(channel.getId());

    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("deleteAllByChannelId success")
  void deleteAllByChannelId_success() {
    User user = createUser();
    Channel channel = createChannel();

    createMessage(channel, user);

    messageRepository.deleteAllByChannelId(channel.getId());

    assertThat(messageRepository.findAll()).isEmpty();
  }
}
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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(RepositoryTestConfig.class)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  void findAllByChannelIdWithAuthor_shouldReturnMessagesWithAuthor_whenCursorValid() {
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    User author = saveUser("alice", "alice@test.com");

    Message m1 = messageRepository.save(new Message("m1", channel, author, List.of()));
    Message m2 = messageRepository.save(new Message("m2", channel, author, List.of()));

    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        m2.getCreatedAt().plusSeconds(1),
        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
    );

    assertThat(result.getContent())
        .hasSize(2)
        .allSatisfy(m -> assertThat(m.getAuthor()).isNotNull());
  }

  @Test
  void findAllByChannelIdWithAuthor_shouldReturnEmpty_whenCursorTooOld() {
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    User author = saveUser("bob", "bob@test.com");

    messageRepository.save(new Message("m1", channel, author, List.of()));

    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        Instant.EPOCH,
        PageRequest.of(0, 10)
    );

    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void findLastMessageAtByChannelId_shouldReturnLatestTimestamp_whenMessagesExist() {
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    User author = saveUser("charlie", "charlie@test.com");

    Message oldMsg = messageRepository.save(new Message("old", channel, author, List.of()));
    Message newMsg = messageRepository.save(new Message("new", channel, author, List.of()));

    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    assertThat(result).isPresent();
    assertThat(result.get()).isAfterOrEqualTo(oldMsg.getCreatedAt());
    assertThat(result.get()).isAfterOrEqualTo(newMsg.getCreatedAt());
  }

  @Test
  void findLastMessageAtByChannelId_shouldReturnEmpty_whenNoMessages() {
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));

    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    assertThat(result).isEmpty();
  }

  private User saveUser(String username, String email) {
    User user = new User(username, email, "password123", null);
    new UserStatus(user, Instant.now());
    return userRepository.save(user);
  }
}
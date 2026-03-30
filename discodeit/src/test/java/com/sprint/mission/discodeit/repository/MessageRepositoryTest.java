package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserStatusRepository userStatusRepository;

  @Test
  @DisplayName("메시지 저장 및 페이징 조회")
  void findAllByChannelIdSuccess() {
    User author = userRepository.save(new User(
        "tester", "test@example.com", "password123!", null));
    UserStatus status = new UserStatus(author, Instant.now());
    userStatusRepository.save(status);
    Channel channel = channelRepository.save(new Channel(ChannelType.PRIVATE, "자유게시판", "설명"
    ));
    messageRepository.save(new Message("반가워요!", channel, author, new ArrayList<>()
    ));

    messageRepository.flush();

    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        Instant.now().plusSeconds(10),
        PageRequest.of(0, 10, Sort.by("createdAt").descending())
    );

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("메시지 없는 채널 조회")
  void findAllByChannelIdFail() {
    // When
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        UUID.randomUUID(),
        Instant.now(),
        PageRequest.of(0, 10)
    );

    // Then
    assertThat(result.getContent()).isEmpty();
  }
}
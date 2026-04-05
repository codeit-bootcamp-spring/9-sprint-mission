package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class MessageRepositoryTest {

  @Autowired private MessageRepository messageRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ChannelRepository channelRepository;
  @Autowired private TestEntityManager entityManager;

  @Test
  @DisplayName("채널 내 메시지 페이징 및 정렬(최신순) 조회 - 성공")
  void findAllByChannelIdWithAuthor_Paging_Success() throws InterruptedException {
    // Given
    User author = new User("author", "a@e.com", "p", null);

    UserStatus status = new UserStatus(author, Instant.now());
    ReflectionTestUtils.setField(author, "status", status);
    userRepository.save(author);

    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "ch", "desc"));

    messageRepository.save(new Message("First", channel, author, new ArrayList<>()));
    Thread.sleep(10);
    messageRepository.save(new Message("Second", channel, author, new ArrayList<>()));

    entityManager.flush();
    entityManager.clear();

    // When
    Pageable pageable = PageRequest.of(0, 1, Sort.by("createdAt").descending());
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(), Instant.now().plusSeconds(10), pageable
    );

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getContent()).isEqualTo("Second");
    assertThat(result.hasNext()).isTrue();
    assertThat(result.getContent().get(0).getAuthor().getUsername()).isEqualTo("author");
  }

  @Test
  @DisplayName("채널 내 메시지 페이징 조회 - 실패 (존재하지 않는 채널 ID)")
  void findAllByChannelIdWithAuthor_Paging_Fail() {
    // Given
    UUID fakeChannelId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10);

    // When
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        fakeChannelId, Instant.now(), pageable
    );

    // Then
    assertThat(result.getContent()).isEmpty();
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  @DisplayName("채널의 마지막 메시지 시간 조회 - 성공")
  void findLastMessageAtByChannelId_Success() {
    // Given
    User author = userRepository.save(new User("u", "u@e.com", "p", null));
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "ch", "desc"));

    messageRepository.save(new Message("Hello", channel, author, new ArrayList<>()));

    // When
    Optional<Instant> lastAt = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // Then
    assertThat(lastAt).isPresent();
  }

  @Test
  @DisplayName("채널의 마지막 메시지 시간 조회 - 실패 (메시지가 없는 경우)")
  void findLastMessageAtByChannelId_Fail() {
    // Given
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "empty_ch", "desc"));

    // When
    Optional<Instant> lastAt = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // Then
    assertThat(lastAt).isEmpty();
  }
}
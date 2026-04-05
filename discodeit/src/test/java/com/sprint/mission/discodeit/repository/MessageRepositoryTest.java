package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private TestEntityManager entityManager;

  private Channel channel;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User("user", "user@email.com", "pass", null);
    entityManager.persist(user);

    channel = new Channel(ChannelType.PUBLIC, "Test Channel", "Desc");
    entityManager.persist(channel);

    // 3개의 메시지를 시간차를 두고 저장
    entityManager.persist(new Message("Message 1", channel, user, List.of()));
    entityManager.flush(); // ID, createdAt 생성을 위해 flush
    try { Thread.sleep(10); } catch (InterruptedException e) {}
    entityManager.persist(new Message("Message 2", channel, user, List.of()));
    entityManager.flush();
    try { Thread.sleep(10); } catch (InterruptedException e) {}
    entityManager.persist(new Message("Message 3", channel, user, List.of()));
    entityManager.flush();
  }

  @Test
  @DisplayName("findAllByChannelIdWithAuthor (페이징/정렬) - 성공 (첫 페이지)")
  void findAllByChannelIdWithAuthor_Paging_Success() {
    // given
    // createdAt 기준으로 내림차순 정렬, 한 페이지에 2개씩
    Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

    // when
    Slice<Message> messages = messageRepository.findAllByChannelIdWithAuthor(channel.getId(), Instant.now(), pageable);

    // then
    assertThat(messages.hasContent()).isTrue();
    assertThat(messages.getNumberOfElements()).isEqualTo(2);
    assertThat(messages.hasNext()).isTrue(); // 다음 페이지가 있어야 함
    assertThat(messages.getContent().get(0).getContent()).isEqualTo("Message 3"); // 최신 메시지
    assertThat(messages.getContent().get(1).getContent()).isEqualTo("Message 2");
  }

  @Test
  @DisplayName("findAllByChannelIdWithAuthor (페이징/정렬) - 실패 (메시지 없는 채널)")
  void findAllByChannelIdWithAuthor_Paging_Fail() {
    // given
    Channel emptyChannel = new Channel(ChannelType.PUBLIC, "Empty", null);
    entityManager.persist(emptyChannel);
    Pageable pageable = PageRequest.of(0, 10);

    // when
    Slice<Message> messages = messageRepository.findAllByChannelIdWithAuthor(emptyChannel.getId(), Instant.now(), pageable);

    // then
    assertThat(messages.hasContent()).isFalse();
    assertThat(messages.getNumberOfElements()).isZero();
  }
}
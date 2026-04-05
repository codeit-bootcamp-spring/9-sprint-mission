package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.*;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
@Import(JpaAuditingConfig.class)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  private Channel channel;
  private User user;
  private Message message;

  @BeforeEach
  void setUp() {
    channel = new Channel(ChannelType.PUBLIC, "테스트채널", "설명");
    channelRepository.save(channel);

    user = new User("홍길동", "test@test.com", "password123", null);
    userRepository.save(user);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    userStatusRepository.save(userStatus);

    message = new Message("안녕하세요", channel, user, List.of());
    messageRepository.save(message);
  }

  @Test
  @DisplayName("채널 메시지 페이징 조회 - 성공")
  void findAllByChannelIdWithAuthor_success() {
    // given
    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Instant now = Instant.now().plusSeconds(1); // 저장된 메시지보다 미래 시간

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        now,
        pageable
    );

    // then
    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getContent()).isEqualTo("안녕하세요");
  }

  @Test
  @DisplayName("채널 메시지 페이징 조회 - 커서 이전 메시지 없음")
  void findAllByChannelIdWithAuthor_empty() {
    // given
    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Instant past = Instant.now().minusSeconds(10); // 저장된 메시지보다 과거 시간

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        past,
        pageable
    );

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  @DisplayName("마지막 메시지 시간 조회 - 성공")
  void findLastMessageAtByChannelId_success() {
    // when
    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // then
    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("마지막 메시지 시간 조회 - 메시지 없음")
  void findLastMessageAtByChannelId_empty() {
    // given
    messageRepository.deleteAll();

    // when
    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("채널 메시지 전체 삭제 - 성공")
  void deleteAllByChannelId_success() {
    // when
    messageRepository.deleteAllByChannelId(channel.getId());

    // then
    List<Message> result = messageRepository.findAll();
    assertThat(result).isEmpty();
  }
}
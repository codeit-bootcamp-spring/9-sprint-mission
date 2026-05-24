package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class MessageRepositoryTest {

  @Autowired private MessageRepository messageRepository;

  @Autowired private ChannelRepository channelRepository;
  @Autowired private UserRepository userRepository;

  private Channel testChannel;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = userRepository.save(new User("tester", "test@test.com", "pw", null));
    testChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "테스트 채널", "설명"));
  }

  @Test
  @DisplayName("채널 ID와 커서 기반 페이징 및 정렬 조회 - 성공 (최신순 정렬 확인)")
  void findAllByChannelId_Success_PagingAndSorting() throws InterruptedException {

    messageRepository.save(new Message("첫 번째 메시지", testChannel, testUser, Collections.emptyList()));
    Thread.sleep(10);
    messageRepository.save(new Message("두 번째 메시지", testChannel, testUser, Collections.emptyList()));
    Thread.sleep(10);
    messageRepository.save(new Message("세 번째 메시지", testChannel, testUser, Collections.emptyList()));

    Instant cursor = Instant.now().plusSeconds(10);
    Pageable pageable = PageRequest.of(0, 2);

    Page<Message> result = messageRepository.findAllByChannelId(testChannel.getId(), cursor, pageable);

    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getContent()).isEqualTo("세 번째 메시지");
    assertThat(result.getContent().get(1).getContent()).isEqualTo("두 번째 메시지");
  }

  @Test
  @DisplayName("채널 ID와 커서 기반 페이징 - 실패 (조건에 맞는 과거 데이터가 없음)")
  void findAllByChannelId_Fail_EmptyResult() {
    messageRepository.save(new Message("테스트 메시지", testChannel, testUser, Collections.emptyList()));

    Instant oldCursor = Instant.EPOCH;
    Pageable pageable = PageRequest.of(0, 10);

    Page<Message> result = messageRepository.findAllByChannelId(testChannel.getId(), oldCursor, pageable);

    assertThat(result.isEmpty()).isTrue();
    assertThat(result.getContent()).isEmpty();
  }
}
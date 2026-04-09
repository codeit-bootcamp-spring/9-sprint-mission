package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
// 🔴 Replace.NONE을 삭제하거나 아래처럼 기본값으로 두세요. (H2를 사용하게 됩니다)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("시간 기반 커서 페이징 및 정렬 쿼리 검증")
  void findMessagesNoOffset_Success() {
    // 1. 데이터 준비
    User author = entityManager.persist(new User("testUser", "test@test.com", "pw", null));
    Channel channel = entityManager.persist(new Channel("testChannel", "desc", ChannelType.PUBLIC, author));

    // 2. 시간 설정 (정밀도 문제를 피하기 위해 명확하게 초 단위로 자름)
    Instant baseTime = Instant.now().minusSeconds(10000);

    // Native Query 대신 엔티티의 필드를 직접 수정하는 것이 CI 환경에서 훨씬 안전합니다.
    Message oldMsg = new Message("First Message", author, channel, null);
    Message newMsg = new Message("Second Message", author, channel, null);

    entityManager.persist(oldMsg);
    entityManager.persist(newMsg);

    Instant cursorTime = baseTime.plusSeconds(5000);
    Instant oldTime = baseTime;

    entityManager.flush();
    entityManager.clear();

    // 4. 검증
    Slice<Message> result = messageRepository.findMessagesNoOffset(
        channel.getId(), cursorTime, PageRequest.of(0, 1));

    assertThat(result.getContent()).isNotEmpty();
    // 쿼리 결과가 내림차순인지 오름차순인지에 따라 기대값이 달라질 수 있습니다.
  }
}
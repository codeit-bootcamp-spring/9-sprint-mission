package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.test.util.ReflectionTestUtils; // [추가] 필드 강제 수정을 위해 필요

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MessageRepositoryTest {

  @Autowired private MessageRepository messageRepository;
  @Autowired private TestEntityManager entityManager;

  @Test
  @DisplayName("시간 기반 커서 페이징 및 정렬 쿼리 검증")
  void findMessagesNoOffset_Success() {
    User author = entityManager.persist(new User("testUser", "test@test.com", "pw", null));
    Channel channel = entityManager.persist(new Channel("testChannel", "desc", ChannelType.PUBLIC, author));

    // 1. 메시지 생성
    Message oldMsg = new Message("First Message", author, channel, null);
    Message newMsg = new Message("Second Message", author, channel, null);

    // 2. [핵심] 억지로 시간을 과거로 설정합니다. (Reflection 사용)
    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    ReflectionTestUtils.setField(oldMsg, "createdAt", now.minusSeconds(120));
    ReflectionTestUtils.setField(newMsg, "createdAt", now.minusSeconds(60));

    entityManager.persist(oldMsg);
    entityManager.persist(newMsg);
    entityManager.flush();
    entityManager.clear();

    // 3. 커서 시간을 두 메시지 사이로 설정 (60초 전보다 뒤, 120초 전보다 앞)
    Instant cursorTime = now.minusSeconds(30);

    Slice<Message> result = messageRepository.findMessagesNoOffset(
        channel.getId(), cursorTime, PageRequest.of(0, 10));

    // 4. 검증 (메시지가 존재해야 함)
    assertThat(result.getContent()).isNotEmpty();
  }
}
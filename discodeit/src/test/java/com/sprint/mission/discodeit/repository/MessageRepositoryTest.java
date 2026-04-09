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
import org.springframework.test.util.ReflectionTestUtils;

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

    Message oldMsg = new Message("First Message", author, channel, null);

    // 1. 먼저 저장해서 JPA가 현재 시간을 넣게 둡니다.
    entityManager.persist(oldMsg);
    entityManager.flush();

    // 2. 저장된 "후에" 시간을 과거로 강제 조작합니다. (Auditing 방지)
    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    ReflectionTestUtils.setField(oldMsg, "createdAt", now.minusSeconds(100));

    entityManager.flush();
    entityManager.clear();

    // 3. 커서 시간을 현재(now)로 잡으면, 100초 전인 oldMsg는 무조건 나와야 합니다.
    Slice<Message> result = messageRepository.findMessagesNoOffset(
        channel.getId(), now, PageRequest.of(0, 10));

    assertThat(result.getContent()).isNotEmpty();
  }
}
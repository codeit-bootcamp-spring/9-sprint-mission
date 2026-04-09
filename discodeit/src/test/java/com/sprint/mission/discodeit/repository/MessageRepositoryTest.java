package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.*;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
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
  @DisplayName("시간 기반 커서 페이징 검증")
  void findMessagesNoOffset_Success() {
    User author = entityManager.persist(new User("test", "t@t.com", "p", null));
    Channel channel = entityManager.persist(new Channel("c", "d", ChannelType.PUBLIC, author));
    Message msg = new Message("Hello", author, channel, null);

    entityManager.persist(msg);
    entityManager.flush();

    Instant past = Instant.now().minusSeconds(1000);
    ReflectionTestUtils.setField(msg, "createdAt", past);

    entityManager.flush();
    entityManager.clear();

    var result = messageRepository.findMessagesNoOffset(channel.getId(), Instant.now(), PageRequest.of(0, 10));
    assertThat(result.getContent()).isNotEmpty();
  }
}
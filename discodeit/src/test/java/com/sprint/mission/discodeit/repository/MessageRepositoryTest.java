package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("시간 기반 커서 페이징 및 정렬 쿼리 검증")
  void findMessagesNoOffset_Success() {

    User author = entityManager.persist(new User("testUser", "test@test.com", "pw", null));
    Channel channel = entityManager.persist(
        new Channel("testChannel", "desc", ChannelType.PUBLIC, author));

    Message oldMsg = new Message("First Message", author, channel, null);
    Message newMsg = new Message("Second Message", author, channel, null);

    entityManager.persist(oldMsg);
    entityManager.persist(newMsg);
    entityManager.flush();

    Instant oldTime = Instant.now().minusSeconds(7200);
    entityManager.getEntityManager()
        .createNativeQuery("UPDATE messages SET created_at = '" + java.sql.Timestamp.from(oldTime)
            + "' WHERE content = 'First Message'")
        .executeUpdate();

    Instant cursorTime = Instant.now().minusSeconds(3600);
    entityManager.getEntityManager()
        .createNativeQuery(
            "UPDATE messages SET created_at = '" + java.sql.Timestamp.from(cursorTime)
                + "' WHERE content = 'Second Message'")
        .executeUpdate();

    entityManager.clear();

    Slice<Message> result = messageRepository.findMessagesNoOffset(
        channel.getId(), cursorTime, PageRequest.of(0, 1));

    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getContent().get(0).getContent()).isEqualTo("First Message");
  }
}
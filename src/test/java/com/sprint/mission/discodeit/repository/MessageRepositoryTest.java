package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager em;

    private Channel channel;
    private User author;

    @BeforeEach
    void setUp() {
        author = new User("testuser", "test@email.com", "password1234", null);
        em.persist(author);

        channel = new Channel(ChannelType.PUBLIC, "general", null);
        em.persist(channel);

        em.flush();
    }

    @Test
    @DisplayName("findAllByChannelId - 채널의 메시지를 페이징으로 조회한다")
    void findAllByChannelId() {
        // given
        for (int i = 0; i < 5; i++) {
            persistMessage("message " + i);
        }
        em.flush();
        em.clear();

        // when
        Slice<Message> slice = messageRepository.findAllByChannelId(
            channel.getId(), PageRequest.of(0, 3));

        // then
        assertThat(slice.getContent()).hasSize(3);
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("findAllByChannelIdAndCreatedAtBefore - 커서 이전 메시지를 조회한다")
    void findAllByChannelIdAndCreatedAtBefore() {
        // given
        persistMessage("old message");
        em.flush();
        em.clear();

        // when
        Slice<Message> slice = messageRepository.findAllByChannelIdAndCreatedAtBefore(
            channel.getId(), Instant.now(), PageRequest.of(0, 50));

        // then
        assertThat(slice.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findLastCreatedAtByChannelId - 채널의 마지막 메시지 시각을 조회한다")
    void findLastCreatedAtByChannelId() {
        // given
        persistMessage("message1");
        persistMessage("message2");
        em.flush();
        em.clear();

        // when
        Optional<Instant> lastCreatedAt = messageRepository.findLastCreatedAtByChannelId(
            channel.getId());

        // then
        assertThat(lastCreatedAt).isPresent();
    }

    @Test
    @DisplayName("findLastCreatedAtByChannelIds - 여러 채널의 마지막 메시지 시각을 일괄 조회한다")
    void findLastCreatedAtByChannelIds() {
        // given
        persistMessage("msg");
        em.flush();
        em.clear();

        // when
        List<Object[]> results = messageRepository.findLastCreatedAtByChannelIds(
            List.of(channel.getId()));

        // then
        assertThat(results).hasSize(1);
    }

    private Message persistMessage(String content) {
        Message message = new Message(content, channel, author, List.of());
        return em.persist(message);
    }
}

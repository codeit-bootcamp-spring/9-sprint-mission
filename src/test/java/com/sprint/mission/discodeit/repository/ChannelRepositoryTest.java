package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("findAllByUserWithDetails - PUBLIC 채널과 사용자가 참여한 PRIVATE 채널을 조회한다")
    void findAllByUserWithDetails() {
        // given
        User user = new User("testuser", "test@email.com", "password1234", null);
        em.persist(user);

        Channel publicChannel = new Channel(ChannelType.PUBLIC, "general", null);
        em.persist(publicChannel);

        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        em.persist(privateChannel);

        ReadStatus readStatus = new ReadStatus(user, privateChannel, Instant.now());
        em.persist(readStatus);

        em.flush();
        em.clear();

        // when
        List<Channel> channels = channelRepository.findAllByUserWithDetails(
            ChannelType.PUBLIC, user.getId());

        // then
        assertThat(channels).hasSize(2);
    }

    @Test
    @DisplayName("findAllByUserWithDetails - 참여하지 않은 PRIVATE 채널은 조회되지 않는다")
    void findAllByUserWithDetails_excludeNonParticipant() {
        // given
        User user = new User("testuser", "test@email.com", "password1234", null);
        em.persist(user);

        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        em.persist(privateChannel);

        em.flush();
        em.clear();

        // when
        List<Channel> channels = channelRepository.findAllByUserWithDetails(
            ChannelType.PUBLIC, user.getId());

        // then
        assertThat(channels).isEmpty();
    }
}

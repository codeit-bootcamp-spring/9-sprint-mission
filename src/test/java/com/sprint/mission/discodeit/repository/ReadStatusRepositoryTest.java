package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
class ReadStatusRepositoryTest {

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("사용자-채널 조합으로 ReadStatus를 조회한다")
    void findByUser_IdAndChannel_Id() {
        User user = new User("user1", "user1@test.com", "password1", null);
        em.persist(user);
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        em.persist(channel);
        ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
        em.persist(readStatus);
        em.flush();
        em.clear();

        Optional<ReadStatus> found = readStatusRepository.findByUser_IdAndChannel_Id(
            user.getId(), channel.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(found.get().getChannel().getId()).isEqualTo(channel.getId());
    }

    @Test
    @DisplayName("사용자 ID로 ReadStatus 목록을 조회한다")
    void findAllByUser_Id() {
        User user = new User("user1", "user1@test.com", "password1", null);
        em.persist(user);
        Channel ch1 = new Channel(ChannelType.PUBLIC, "ch1", null);
        Channel ch2 = new Channel(ChannelType.PUBLIC, "ch2", null);
        em.persist(ch1);
        em.persist(ch2);
        em.persist(new ReadStatus(user, ch1, Instant.now()));
        em.persist(new ReadStatus(user, ch2, Instant.now()));
        em.flush();
        em.clear();

        List<ReadStatus> result = readStatusRepository.findAllByUser_Id(user.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 사용자-채널 조합은 빈 Optional을 반환한다")
    void findByUser_IdAndChannel_Id_notFound() {
        Optional<ReadStatus> result = readStatusRepository.findByUser_IdAndChannel_Id(
            java.util.UUID.randomUUID(), java.util.UUID.randomUUID());

        assertThat(result).isEmpty();
    }
}

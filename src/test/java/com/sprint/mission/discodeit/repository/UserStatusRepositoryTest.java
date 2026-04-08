package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
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
class UserStatusRepositoryTest {

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("사용자 ID로 UserStatus를 조회한다")
    void findByUser_Id() {
        User user = new User("user1", "user1@test.com", "password1", null);
        em.persist(user);
        UserStatus status = new UserStatus(user, Instant.now());
        em.persist(status);
        em.flush();
        em.clear();

        Optional<UserStatus> found = userStatusRepository.findByUser_Id(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID는 빈 Optional을 반환한다")
    void findByUser_Id_notFound() {
        Optional<UserStatus> result = userStatusRepository.findByUser_Id(UUID.randomUUID());

        assertThat(result).isEmpty();
    }
}

package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByUsername - 사용자명으로 조회한다")
    void findByUsername() {
        // given
        User user = new User("testuser", "test@email.com", "password1234", null);
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByUsername("testuser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("findByUsername - 존재하지 않는 사용자명은 빈 Optional 반환")
    void findByUsername_notFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail - 이메일 존재 여부를 확인한다")
    void existsByEmail() {
        // given
        userRepository.save(new User("testuser", "test@email.com", "password1234", null));

        // when & then
        assertThat(userRepository.existsByEmail("test@email.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@email.com")).isFalse();
    }

    @Test
    @DisplayName("existsByUsername - 사용자명 존재 여부를 확인한다")
    void existsByUsername() {
        // given
        userRepository.save(new User("testuser", "test@email.com", "password1234", null));

        // when & then
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
        assertThat(userRepository.existsByUsername("other")).isFalse();
    }

    @Test
    @DisplayName("findAllWithDetails - 모든 사용자를 상세 정보와 함께 조회한다")
    void findAllWithDetails() {
        // given
        userRepository.save(new User("user1", "user1@email.com", "password1234", null));
        userRepository.save(new User("user2", "user2@email.com", "password1234", null));

        // when
        List<User> users = userRepository.findAllWithDetails();

        // then
        assertThat(users).hasSize(2);
    }
}

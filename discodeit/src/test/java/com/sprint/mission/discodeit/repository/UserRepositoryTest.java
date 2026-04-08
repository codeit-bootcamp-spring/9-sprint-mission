package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  private User createUser(String username, String email) {
    User user = new User(username, email, "pw", null);
    User saved = userRepository.save(user);
    UserStatus status = new UserStatus(saved, Instant.now());
    userStatusRepository.save(status);
    return saved;
  }

  @Test
  @DisplayName("save success")
  void save_success() {
    User user = new User("user1", "user1@test.com", "pw", null);

    User saved = userRepository.save(user);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getUsername()).isEqualTo("user1");
  }

  @Test
  @DisplayName("findByUsername success")
  void findByUsername_success() {
    createUser("user1", "user1@test.com");

    Optional<User> result = userRepository.findByUsername("user1");

    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("findByUsername fail")
  void findByUsername_fail() {
    Optional<User> result = userRepository.findByUsername("not-exist");

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("existsByEmail success")
  void existsByEmail_success() {
    createUser("user1", "user1@test.com");

    boolean result = userRepository.existsByEmail("user1@test.com");

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("existsByEmail fail")
  void existsByEmail_fail() {
    boolean result = userRepository.existsByEmail("none@test.com");

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("existsByUsername success")
  void existsByUsername_success() {
    createUser("user1", "user1@test.com");

    boolean result = userRepository.existsByUsername("user1");

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("existsByUsername fail")
  void existsByUsername_fail() {
    boolean result = userRepository.existsByUsername("none");

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("findAllWithProfileAndStatus success")
  void findAllWithProfileAndStatus_success() {
    createUser("user1", "user1@test.com");
    createUser("user2", "user2@test.com");

    List<User> result = userRepository.findAllWithProfileAndStatus();

    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).getStatus()).isNotNull();
  }

  @Test
  @DisplayName("findAllWithProfileAndStatus empty")
  void findAllWithProfileAndStatus_empty() {
    List<User> result = userRepository.findAllWithProfileAndStatus();

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("unique username constraint")
  void unique_username_constraint() {
    createUser("user1", "user1@test.com");

    assertThatThrownBy(() -> {
      userRepository.save(new User("user1", "user2@test.com", "pw", null));
      userRepository.flush();
    }).isInstanceOf(Exception.class);
  }

  @Test
  @DisplayName("findById fail")
  void findById_fail() {
    Optional<User> result = userRepository.findById(UUID.randomUUID());

    assertThat(result).isEmpty();
  }
}
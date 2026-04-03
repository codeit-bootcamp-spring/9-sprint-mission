package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserStatusRepository userStatusRepository; // Status 저장을 위해 필요

  @Test
  @DisplayName("이름 유저 조회")
  void findByUsernameSuccess() {
    User user = new User("tester", "test@test.com", "pw123", null);
    userRepository.save(user);
    Optional<User> found = userRepository.findByUsername("tester");

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@test.com");
  }

  @Test
  @DisplayName("없는 유저 조회")
  void findByUsernameFail() {
    Optional<User> found = userRepository.findByUsername("noname");

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("이메일,유저 이름 중복 확인")
  void existsByEmailAndUsernameSuccess() {
    userRepository.save(new User("uniqueUser", "unique@test.com", "pw123", null));

    assertThat(userRepository.existsByEmail("unique@test.com")).isTrue();
    assertThat(userRepository.existsByUsername("uniqueUser")).isTrue();
    assertThat(userRepository.existsByEmail("other@test.com")).isFalse();
  }

  @Test
  @DisplayName("모든 유저 목록 조회")
  void findAllWithProfileAndStatusSuccess() {
    User user = userRepository.save(new User("fetchUser", "fetch@test.com", "pw123", null));

    UserStatus status = new UserStatus(user, Instant.now());
    userStatusRepository.save(status);

    userRepository.flush();

    List<User> users = userRepository.findAllWithProfileAndStatus();

    assertThat(users.get(0).getUsername()).isEqualTo("fetchUser");
  }
}
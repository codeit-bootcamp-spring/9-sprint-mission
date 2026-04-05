package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("이메일 존재 여부 확인 - 성공 (존재하는 이메일)")
  void existsByEmail_Success() {
    // Given
    User user = new User("tester", "test@email.com", "password", null);
    userRepository.save(user);

    // When
    boolean exists = userRepository.existsByEmail("test@email.com");

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("이메일 존재 여부 확인 - 실패 (존재하지 않는 이메일)")
  void existsByEmail_Fail() {
    // When
    boolean exists = userRepository.existsByEmail("none@email.com");

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("프로필과 상태를 포함한 조회 - 성공")
  void findAllWithProfileAndStatus_Success() {
    // Given
    User user = new User("activeUser", "active@email.com", "pass", null);
    UserStatus status = new UserStatus(user, Instant.now());
    ReflectionTestUtils.setField(user, "status", status);

    userRepository.save(user);
    entityManager.flush();
    entityManager.clear();

    // When
    List<User> users = userRepository.findAllWithProfileAndStatus();

    // Then
    assertThat(users).isNotEmpty();
    assertThat(users.get(0).getStatus()).isNotNull();
  }

  @Test
  @DisplayName("프로필과 상태를 포함한 조회 - 실패 (데이터 없음)")
  void findAllWithProfileAndStatus_Fail() {

    // When
    List<User> users = userRepository.findAllWithProfileAndStatus();

    // Then
    assertThat(users).isEmpty();
  }
}
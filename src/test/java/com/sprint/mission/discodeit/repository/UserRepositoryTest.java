package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.*;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("홍길동", "test@test.com", "password123", null);
    userRepository.save(user);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    userStatusRepository.save(userStatus);
  }

  @Test
  @DisplayName("이메일로 유저 존재 확인 - 존재함")
  void existsByEmail_true() {
    boolean result = userRepository.existsByEmail("test@test.com");
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("이메일로 유저 존재 확인 - 존재하지 않음")
  void existsByEmail_false() {
    boolean result = userRepository.existsByEmail("none@test.com");
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("유저명으로 유저 존재 확인 - 존재함")
  void existsByUsername_true() {
    boolean result = userRepository.existsByUsername("홍길동");
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("유저명으로 유저 존재 확인 - 존재하지 않음")
  void existsByUsername_false() {
    boolean result = userRepository.existsByUsername("없는유저");
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("프로필과 상태 포함 전체 유저 조회 - 성공")
  void findAllWithProfileAndStatus_success() {
    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUsername()).isEqualTo("홍길동");
  }

  @Test
  @DisplayName("프로필과 상태 포함 전체 유저 조회 - 유저 없음")
  void findAllWithProfileAndStatus_empty() {
    // given
    userStatusRepository.deleteAll();
    userRepository.deleteAll();

    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).isEmpty();
  }
}
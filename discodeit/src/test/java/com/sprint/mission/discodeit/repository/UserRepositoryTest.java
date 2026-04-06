package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository 슬라이스 테스트")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    User user = new User("tester", "test@sprint.com", "password123", null);
    UserStatus status = new UserStatus(user, Instant.now());
    user.setStatus(status);

    entityManager.persist(user);
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  @DisplayName("사용자명으로 조회 - 성공")
  void findByUsername_Success() {
    // given
    String username = "tester";

    // when
    Optional<User> found = userRepository.findByUsername(username);

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getUsername()).isEqualTo(username);
    assertThat(found.get().getId()).isNotNull();
  }

  @Test
  @DisplayName("전체 사용자 조회 (Fetch Join) - 성공")
  void findAll_Success() {
    // given & when
    List<User> users = userRepository.findAll();

    // then
    assertThat(users).isNotEmpty();
    assertThat(users.get(0).getStatus()).isNotNull();
  }

  @Test
  @DisplayName("이메일 중복 확인 - 성공")
  void existsByEmail_True() {
    // given
    String email = "test@sprint.com";

    // when
    boolean exists = userRepository.existsByEmail(email);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 ID로 존재 여부 확인 - 실패")
  void existsById_False() {
    // given
    UUID randomId = UUID.randomUUID();

    // when
    boolean exists = userRepository.existsById(randomId);

    // then
    assertThat(exists).isFalse();
  }
}
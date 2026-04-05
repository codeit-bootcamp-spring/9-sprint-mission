package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing // createdAt 등 Auditing 필드 활성화
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager entityManager; // 테스트용 데이터 준비를 위한 유틸리티

  @Test
  @DisplayName("findByUsername - 성공")
  void findByUsername_Success() {
    // given
    User user = new User("testuser", "test@email.com", "password", null);
    entityManager.persist(user);

    // when
    Optional<User> foundUser = userRepository.findByUsername("testuser");

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("findByUsername - 실패 (존재하지 않는 사용자)")
  void findByUsername_Fail() {
    // when
    Optional<User> foundUser = userRepository.findByUsername("nonexistent");

    // then
    assertThat(foundUser).isNotPresent();
  }

  @Test
  @DisplayName("findAllWithProfileAndStatus (커스텀 쿼리) - 성공")
  void findAllWithProfileAndStatus_Success() {
    // given
    User user1 = new User("user1", "user1@email.com", "password", null);
    User user2 = new User("user2", "user2@email.com", "password", null);
    entityManager.persist(user1);
    entityManager.persist(user2);

    // when
    List<User> users = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(users).hasSize(2);
    // Fetch Join으로 profile, status를 함께 가져왔는지 확인 (N+1 문제 방지)
    // 실제 쿼리 로그를 통해 join 구문이 실행되었는지 확인하는 것이 더 정확합니다.
    assertThat(users.get(0).getProfile()).isNull(); // 테스트에서는 profile을 null로 넣었으므로
  }
}
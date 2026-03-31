package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("이메일 중복 체크 쿼리 검증 - 존재하는 경우와 존재하지 않는 경우")
  void existsByEmail_Test() {

    String email = "test@example.com";
    entityManager.persist(new User("tester", email, "password", null));

    assertThat(userRepository.existsByEmail(email)).isTrue();
    assertThat(userRepository.existsByEmail("none@example.com")).isFalse();
  }

  @Test
  @DisplayName("사용자 이름으로 조회 성공")
  void findByUsername_Success() {

    String username = "minju_gu";
    entityManager.persist(new User(username, "minju@test.com", "pass123", null));

    Optional<User> foundUser = userRepository.findByUsername(username);

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo(username);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 이름 조회 시 빈 Optional 반환")
  void findByUsername_Fail_NotFound() {

    Optional<User> foundUser = userRepository.findByUsername("anonymous");

    assertThat(foundUser).isEmpty();
  }
}
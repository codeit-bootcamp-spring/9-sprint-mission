package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("유저 저장 및 조회 성공 테스트")
  void save_and_find_user_success() {
    // Given
    User user = new User("tester", "test@example.com", "password123", null);

    // When
    User savedUser = userRepository.save(user);

    // Then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 유저 조회 실패 테스트")
  void find_user_fail_by_invalid_email() {
    // Given
    String invalidEmail = "none@example.com";

    // When
    // UserRepository에 추가했던 findByEmail 메서드를 호출합니다.
    Optional<User> foundUser = userRepository.findByEmail(invalidEmail);

    // Then
    assertThat(foundUser).isEmpty();
  }
}
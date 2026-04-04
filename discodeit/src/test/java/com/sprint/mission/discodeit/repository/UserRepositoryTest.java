package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase; // 추가!
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing

class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("이메일 존재 확인 - 성공")
  void existsByEmail_Success_True() {
    User user = new User("tester1", "test1@test.com", "pw", null);
    userRepository.save(user);

    boolean exists = userRepository.existsByEmail("test1@test.com");

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("이메일 존재 확인 - 실패")
  void existsByEmail_Fail_False() {
    User user = new User("tester2", "test2@test.com", "pw", null);
    userRepository.save(user);

    boolean exists = userRepository.existsByEmail("wrong@test.com");

    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("유저네임 존재 확인 - 성공")
  void existsByUsername_Success_True() {
    User user = new User("tester3", "test3@test.com", "pw", null);
    userRepository.save(user);

    boolean exists = userRepository.existsByUsername("tester3");

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("유저네임 존재 확인 - 실패")
  void existsByUsername_Fail_False() {
    User user = new User("tester4", "test4@test.com", "pw", null);
    userRepository.save(user);

    boolean exists = userRepository.existsByUsername("wrong_name");

    assertThat(exists).isFalse();
  }
}
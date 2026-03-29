package com.sprint.mission.discodeit.slice;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("전체 사용자 조회")
  void findAllWithStatusAndProfile_Success() {
    User user1 = new User("user1", "pw1", "user1@test.com", null);
    User user2 = new User("user2", "pw2", "user2@test.com", null);
    userRepository.saveAll(List.of(user1, user2));

    List<User> users = userRepository.findAllWithStatusAndProfile();

    assertThat(users).hasSize(2);
    assertThat(users).extracting("username").containsExactlyInAnyOrder("user1", "user2");
  }

  @Test
  @DisplayName("사용자 이름으로 조회 성공")
  void findByUsername_Success() {
    String username = "test";
    userRepository.save(new User(username, "pass", "test@email.com", null));

    Optional<User> target = userRepository.findByUsername(username);

    assertThat(target).isPresent();
    assertThat(target.get().getUsername()).isEqualTo(username);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 이름 조회 실패")
  void findByUsername_fail() {

    Optional<User> found = userRepository.findByUsername("none");

    assertThat(found).isEmpty();
  }
}

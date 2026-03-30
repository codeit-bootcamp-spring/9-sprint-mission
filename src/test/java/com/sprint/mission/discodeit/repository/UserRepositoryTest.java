package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(RepositoryTestConfig.class)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByUsername_shouldReturnUser_whenExists() {
    User saved = saveUser("alice", "alice@test.com");

    Optional<User> result = userRepository.findByUsername("alice");

    assertThat(result).isPresent();
    assertThat(result.get())
        .extracting(User::getId, User::getUsername)
        .containsExactly(saved.getId(), "alice");
  }

  @Test
  void findByUsername_shouldReturnEmpty_whenNotExists() {
    Optional<User> result = userRepository.findByUsername("no-user");

    assertThat(result).isEmpty();
  }

  @Test
  void findAllWithProfileAndStatus_shouldReturnUsersWithRelations() {
    saveUserWithProfileAndStatus("bob", "bob@test.com");

    List<User> result = userRepository.findAllWithProfileAndStatus();

    assertThat(result)
        .hasSize(1)
        .allSatisfy(user -> {
          assertThat(user.getStatus()).isNotNull();
          assertThat(user.getProfile()).isNotNull();
        });
  }

  @Test
  void findAllWithProfileAndStatus_shouldReturnEmpty_whenNoData() {
    List<User> result = userRepository.findAllWithProfileAndStatus();

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnSortedPage_whenPaging() {
    saveUser("charlie", "c@test.com");
    saveUser("alice", "a@test.com");
    saveUser("bravo", "b@test.com");

    Page<User> page = userRepository.findAll(
        PageRequest.of(0, 2, Sort.by("username"))
    );

    assertThat(page.getContent())
        .extracting(User::getUsername)
        .containsExactly("alice", "bravo");
  }

  @Test
  void findAll_shouldReturnEmpty_whenPageOutOfRange() {
    saveUser("alice", "a@test.com");

    Page<User> page = userRepository.findAll(PageRequest.of(10, 10));

    assertThat(page.getContent()).isEmpty();
  }

  private User saveUser(String username, String email) {
    User user = new User(username, email, "password123", null);
    new UserStatus(user, Instant.now());
    return userRepository.save(user);
  }

  private void saveUserWithProfileAndStatus(String username, String email) {
    BinaryContent profile = new BinaryContent("profile.png", 10L, "image/png");
    User user = new User(username, email, "password123", profile);
    new UserStatus(user, Instant.now());
    userRepository.save(user);
  }
}
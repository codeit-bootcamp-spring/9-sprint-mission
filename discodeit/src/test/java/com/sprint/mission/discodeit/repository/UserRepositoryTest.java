package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private User createUser(String username, String email) {
    User user = new User(username, email, "password", null);
    new UserStatus(user, Instant.now());
    return user;
  }

  @Test
  void findByUsername_success() {
    User user = createUser("testUser", "test@test.com");
    userRepository.save(user);

    Optional<User> result = userRepository.findByUsername("testUser");

    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("test@test.com");
  }

  @Test
  void findByUsername_fail() {
    Optional<User> result = userRepository.findByUsername("noneUser");

    assertThat(result).isEmpty();
  }

  @Test
  void existsByEmail_true() {
    User user = createUser("testUser", "test@test.com");
    userRepository.save(user);

    boolean result = userRepository.existsByEmail("test@test.com");

    assertThat(result).isTrue();
  }

  @Test
  void existsByEmail_false() {
    boolean result = userRepository.existsByEmail("none@test.com");

    assertThat(result).isFalse();
  }

  @Test
  void existsByUsername_true() {
    User user = createUser("testUser", "test@test.com");
    userRepository.save(user);

    boolean result = userRepository.existsByUsername("testUser");

    assertThat(result).isTrue();
  }

  @Test
  void existsByUsername_false() {
    boolean result = userRepository.existsByUsername("noneUser");

    assertThat(result).isFalse();
  }

  @Test
  void findAllWithProfileAndStatus_success() {
    User user = createUser("testUser", "test@test.com");
    userRepository.save(user);

    List<User> result = userRepository.findAllWithProfileAndStatus();

    assertThat(result).isNotEmpty();
    assertThat(result.get(0).getStatus()).isNotNull();
  }

  @Test
  void findAllWithProfileAndStatus_empty() {
    List<User> result = userRepository.findAllWithProfileAndStatus();

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_pagingAndSorting() {
    for (int i = 0; i < 10; i++) {
      User user = createUser("user" + i, "test" + i + "@test.com");
      userRepository.save(user);
    }

    Pageable pageable = PageRequest.of(
        0,
        5,
        Sort.by("username").descending()
    );

    Page<User> result = userRepository.findAll(pageable);

    assertThat(result.getContent().size()).isEqualTo(5);
    assertThat(result.getTotalElements()).isEqualTo(10);

    List<User> users = result.getContent();
    assertThat(users.get(0).getUsername())
        .isGreaterThan(users.get(1).getUsername());
  }

  @Test
  void findAll_paging_empty() {
    Pageable pageable = PageRequest.of(0, 5);

    Page<User> result = userRepository.findAll(pageable);

    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isEqualTo(0);
  }
}
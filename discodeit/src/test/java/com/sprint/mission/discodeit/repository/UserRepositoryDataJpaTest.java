package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryDataJpaTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("findByUsername 성공: 저장된 사용자명을 조회한다")
  void findByUsername_success() {
    User saved = saveUser("jun", "jun@test.com");

    Optional<User> found = userRepository.findByUsername("jun");

    assertTrue(found.isPresent());
    assertEquals(saved.getId(), found.get().getId());
  }

  @Test
  @DisplayName("findByUsername 실패: 존재하지 않는 사용자명은 빈 결과를 반환한다")
  void findByUsername_fail_notFound() {
    saveUser("jun", "jun@test.com");

    Optional<User> found = userRepository.findByUsername("unknown");

    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("existsByEmail 성공: 등록된 이메일이면 true를 반환한다")
  void existsByEmail_success() {
    saveUser("jun", "jun@test.com");

    boolean exists = userRepository.existsByEmail("jun@test.com");

    assertTrue(exists);
  }

  @Test
  @DisplayName("existsByEmail 실패: 없는 이메일이면 false를 반환한다")
  void existsByEmail_fail_notFound() {
    saveUser("jun", "jun@test.com");

    boolean exists = userRepository.existsByEmail("nope@test.com");

    assertFalse(exists);
  }

  @Test
  @DisplayName("findAll 페이징/정렬 성공: username 오름차순으로 1페이지를 조회한다")
  void findAll_success_pagingAndSorting() {
    saveUser("charlie", "charlie@test.com");
    saveUser("alice", "alice@test.com");
    saveUser("bravo", "bravo@test.com");

    Page<User> page = userRepository.findAll(
        PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "username")));

    assertEquals(2, page.getContent().size());
    assertEquals("alice", page.getContent().get(0).getUsername());
    assertEquals("bravo", page.getContent().get(1).getUsername());
    assertTrue(page.hasNext());
  }

  @Test
  @DisplayName("findAll 페이징/정렬 실패: 범위를 벗어난 페이지는 빈 결과를 반환한다")
  void findAll_fail_outOfRangePage() {
    saveUser("alice", "alice@test.com");
    saveUser("bravo", "bravo@test.com");

    Page<User> page = userRepository.findAll(
        PageRequest.of(2, 2, Sort.by(Sort.Direction.ASC, "username")));

    assertTrue(page.getContent().isEmpty());
    assertEquals(2, page.getTotalElements());
  }

  private User saveUser(String username, String email) {
    return userRepository.save(new User(username, email, "password123", null));
  }
}


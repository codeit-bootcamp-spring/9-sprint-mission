package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// UserRepository.java
public interface UserRepository {

  User save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByUsername(String username);

  List<User> findAll();

  boolean existsById(UUID id);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  void deleteById(UUID id);
}
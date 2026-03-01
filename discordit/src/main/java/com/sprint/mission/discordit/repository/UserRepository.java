package com.sprint.mission.discordit.repository;

import com.sprint.mission.discordit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  User save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByUsername(String username);

  List<User> findAll();

  boolean existsById(UUID id);

  void deleteById(UUID id);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}

package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  @Override
  @EntityGraph(attributePaths = {"profile"})
  List<User> findAll();

  @Override
  @EntityGraph(attributePaths = {"profile",})
  Optional<User> findById(UUID id);

  boolean existsByEmail(String email);

  boolean existsByRole(Role role);

  boolean existsByUsername(String username);
}

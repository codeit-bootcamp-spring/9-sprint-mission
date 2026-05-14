package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  boolean existsByRole(UserRole role);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.status LEFT JOIN FETCH u.profile")
  List<User> findAllWithStatus();
}

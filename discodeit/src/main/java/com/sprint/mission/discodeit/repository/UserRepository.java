package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.status LEFT JOIN FETCH u.profile")
  List<User> findAll();

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);
}
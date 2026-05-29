package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  @Query("SELECT u FROM User u "
      + "LEFT JOIN FETCH u.profile "
      + "WHERE u.username = :username")
  Optional<User> findByUsernameWithProfile(@Param("username") String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByRole(Role role);

  List<User> findAllByRole(Role role);

  @Query("SELECT u FROM User u "
      + "LEFT JOIN FETCH u.profile")
  List<User> findAllWithProfile();
}

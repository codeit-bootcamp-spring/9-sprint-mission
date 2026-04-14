package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  User save(User user);

  Optional<User> findById(UUID id);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.status WHERE u.username = :username")
  Optional<User> findByUsername(String username);

  @Query("select u from User u join fetch u.status")
  List<User> findAll();

  boolean existsById(UUID id);

  void deleteById(UUID id);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmailAndIdNot(String newEmail, UUID id);

  boolean existsByUsernameAndIdNot(String username, UUID userId);
}

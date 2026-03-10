package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  // JOIN FETCH를 사용하여 유저를 가져올 때 상태 정보까지 한 번에 로드 [cite: 2026-03-09]
  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.status LEFT JOIN FETCH u.profile")
  List<User> findAll();

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);
}
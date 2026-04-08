package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  /** 모든 사용자를 프로필 이미지 + 온라인 상태와 함께 조회 (N+1 방지) */
  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.profile LEFT JOIN FETCH u.status")
  List<User> findAllWithDetails();
}

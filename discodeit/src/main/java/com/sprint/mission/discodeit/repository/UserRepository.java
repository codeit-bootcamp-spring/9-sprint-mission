package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // N+1 문제 해결을 위해 추가된 즉시 로딩(Fetch Join) 메서드
    @EntityGraph(attributePaths = {"status", "profile"})
    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u "
            + "LEFT JOIN FETCH u.profile "
            + "JOIN FETCH u.status")
    List<User> findAllWithProfileAndStatus();
}
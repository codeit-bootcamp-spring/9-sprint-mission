package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  UserStatus save(UserStatus userStatus);

  Optional<UserStatus> findById(UUID id);

  Optional<UserStatus> findByUser_Id(UUID userId);

  List<UserStatus> findAll();

  boolean existsById(UUID id);

  void deleteById(UUID id);

  @Transactional
  @Modifying
  @Query("DELETE FROM UserStatus s WHERE s.user.id = :userId")
  void deleteByUserId(UUID userId);
}

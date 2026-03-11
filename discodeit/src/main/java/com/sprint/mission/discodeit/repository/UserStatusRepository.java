package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  UserStatus save(UserStatus userStatus);

  Optional<UserStatus> findById(UUID id);

  Optional<UserStatus> findByUser_Id(UUID userId);

  List<UserStatus> findAll();

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteByUser_Id(UUID userId);
}

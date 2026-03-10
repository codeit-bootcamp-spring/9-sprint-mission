package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  // 특정 유저의 온라인 상태 및 활동 정보를 로드하는 드라이버 [cite: 2026-03-04]
  Optional<UserStatus> findByUserId(UUID userId);
}
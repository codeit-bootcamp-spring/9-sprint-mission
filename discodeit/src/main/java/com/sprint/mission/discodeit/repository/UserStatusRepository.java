package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    UserStatus save(UserStatus status);
    Optional<UserStatus> findById(UUID userId);
    List<UserStatus> findAll();
    void delete(UUID id);
    boolean existsById(UUID id);

    // 도메인 핵심(유저별 1개 상태)
    Optional<UserStatus> findByUserId(UUID userId);

}
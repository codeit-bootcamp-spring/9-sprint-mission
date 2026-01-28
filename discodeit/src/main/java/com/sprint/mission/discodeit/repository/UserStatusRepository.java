package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

    void create(UserStatus userStatus);

    UserStatus findById(UUID id);

    /**
     * userId로 단건 조회
     */
    UserStatus findByUserId(UUID userId);

    List<UserStatus> findAll();

    boolean update(UUID id, long lastActiveAt);

    /**
     * 요구사항: updateByUserId (특정 userId의 상태 갱신)
     */
    boolean updateByUserId(UUID userId, long lastActiveAt);

    boolean delete(UUID id);
}


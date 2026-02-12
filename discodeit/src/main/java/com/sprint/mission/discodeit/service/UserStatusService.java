package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UUID userId);
    Optional<UserStatusResponse> findByUserId(UUID userId);
    List<UserStatusResponse> findAll();
    void updateByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    boolean isUserOnline(UUID userId);
}
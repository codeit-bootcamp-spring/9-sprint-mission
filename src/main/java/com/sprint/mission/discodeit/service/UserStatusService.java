package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UUID userId);
    UserStatusResponse findById(UUID id);
    List<UserStatusResponse> findAll();
    void delete(UUID id);
    UserStatus updateStatus(UUID userId, UserStatusUpdateRequest request);
}

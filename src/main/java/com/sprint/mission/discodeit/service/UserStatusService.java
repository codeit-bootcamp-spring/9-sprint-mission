package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusResponse;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UUID userId);
    UserStatusResponse findById(UUID id);
    List<UserStatusResponse> findAll();
    UserStatusResponse touchByUserId(UUID userId);
    void delete(UUID id);
}

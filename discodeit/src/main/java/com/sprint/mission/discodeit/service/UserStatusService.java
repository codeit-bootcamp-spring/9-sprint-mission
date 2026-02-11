package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    String create(UserStatusCreateRequest request);
    UserStatusResponse find(UUID id);
    List<UserStatusResponse> findAll();
    void update(UUID id, UserStatusUpdateRequest request);
    void updateByUserId(UUID userId, UserStatusUpdateRequest request);
    void delete(UUID id);
}
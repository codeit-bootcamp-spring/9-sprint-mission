package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDTO create(UserStatusCreateRequest request);
    UserStatusDTO find(UUID id);
    List<UserStatusDTO> findAll();
    UserStatusDTO update(UserStatusUpdateRequest request);
    UserStatusDTO updateByUserId(UUID userId);
    void delete(UUID id);
}

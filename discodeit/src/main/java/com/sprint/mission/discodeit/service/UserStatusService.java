package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatusView create(UserStatusCreateRequest request);
    Optional<UserStatusView> findById(UUID userStatusId);
    List<UserStatusView> findAll();
    UserStatusView update(UserStatusUpdateRequest request);
    UserStatusView updateByUserId(UUID userId, UserStatusUpdateRequest.Params params);
    void delete(UUID userStatusId);
    boolean existsById(UUID userStatusId);
}
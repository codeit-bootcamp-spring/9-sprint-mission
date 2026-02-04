package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusCreateRequest request);
    UserStatusResponse find(UUID userStatusId);
    List<UserStatusResponse> findAll();
    UserStatusResponse update(UserStatusUpdateRequest request);
    UserStatusResponse updateByUserId(UUID userId);
    void delete(UUID userStatusId);
}
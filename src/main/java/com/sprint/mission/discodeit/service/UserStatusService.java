package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusDto create(UUID userId);

    UserStatusDto findById(UUID id);

    List<UserStatusDto> findAll();

    void delete(UUID id);

    UserStatusDto updateStatus(UUID userId, UserStatusUpdateRequest request);
}
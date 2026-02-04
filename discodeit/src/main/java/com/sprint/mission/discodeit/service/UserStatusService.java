package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserStatusService.Request.CreateUserStatusRequest;
import com.sprint.mission.discodeit.DTO.UserStatusService.Request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(CreateUserStatusRequest request);

    UserStatus find(UUID id);

    List<UserStatus> findAll();

    UserStatus update(UpdateUserStatusRequest request);

    UserStatus updateByUserId(UpdateUserStatusRequest request);

    void delete(UUID id);
}

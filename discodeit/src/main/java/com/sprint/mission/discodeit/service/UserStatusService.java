package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.DTO.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.service.DTO.UserStatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest request);
    UserStatus find(UUID id);
    List<UserStatus> findAll();
    UserStatus update(UserStatusUpdateByUserIdRequest request);
    UserStatus updateByUserId(UserStatusUpdateByUserIdRequest request);
    void delete(UUID id);
}

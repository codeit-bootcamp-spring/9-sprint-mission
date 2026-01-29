package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatus status);
    UserStatus findId(UUID id);
    List<UserStatus> findAll();
    UserStatus updateByUserId(UUID userid);
    boolean delete(UUID id);

}

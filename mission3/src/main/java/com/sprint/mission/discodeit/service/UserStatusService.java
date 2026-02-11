package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusDto.createUserStatus createUserStatus);
    UserStatus findById(UUID id);
    List<UserStatus> findAll();
    UserStatus update(UserStatusDto.updateUserStatus userStatus);
    UserStatus updateByUserId(UUID userid);
    boolean delete(UUID id);

}

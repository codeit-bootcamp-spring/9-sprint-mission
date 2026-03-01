package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discordit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discordit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatus create(UserStatusCreateRequest request);

  UserStatus find(UUID userStatusId);

  List<UserStatus> findAll();

  UserStatus update(UUID userStatusId, UserStatusUpdateRequest request);

  UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);

  void delete(UUID userStatusId);
}

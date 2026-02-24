package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  void create(UUID userId);

  UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);

  boolean isUserOnline(UUID userId);

  UserStatus findByUserId(UUID userId);

  List<UserStatus> findAll();

  void deleteByUserId(UUID userId);
}
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  void create(UUID userId);

  UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

  boolean isUserOnline(UUID userId);

  UserStatusDto findByUserId(UUID userId);

  List<UserStatusDto> findAll();

  void deleteByUserId(UUID userId);
}
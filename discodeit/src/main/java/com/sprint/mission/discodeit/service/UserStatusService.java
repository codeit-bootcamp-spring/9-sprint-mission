package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusResponse create(UserStatusCreateRequest request);

  UserStatusResponse find(UUID userStatusId);

  List<UserStatusResponse> findAll();

  UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request);

  UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request);

  void delete(UUID userStatusId);
}

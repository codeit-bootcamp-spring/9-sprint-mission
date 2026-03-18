package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserStatusService {

  UserStatusDto create(UserStatusCreateRequest request);

  UserStatusDto find(UUID userStatusId);

  Slice<UserStatusDto> findAll(Pageable pageable);

  UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

  UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

  void delete(UUID userStatusId);
}

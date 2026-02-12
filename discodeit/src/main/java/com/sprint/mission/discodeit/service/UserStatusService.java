package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto.Response create(UUID userId);
    Optional<UserStatusDto.Response> findByUserId(UUID userId);
    List<UserStatusDto.Response> findAll();
    void updateByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    boolean isUserOnline(UUID userId);
}
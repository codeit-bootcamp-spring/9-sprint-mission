package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<UserDto> create(UserCreateRequest request);
    Optional<UserDto> findById(UUID id);
    List<UserDto> findAll();
    Optional<UserDto> update(UUID id, UserUpdateRequest request);
    boolean delete(UUID id);
}
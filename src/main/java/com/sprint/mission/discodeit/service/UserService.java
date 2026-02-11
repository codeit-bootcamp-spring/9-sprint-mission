package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(CreateUserRequest request);
    UserResponse findById(UUID userId);
    List<UserDto> findAll();
    UserResponse update(UUID userId, UpdateUserRequest request);
    void delete(UUID userId);
}

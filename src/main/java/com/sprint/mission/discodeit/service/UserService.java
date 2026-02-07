package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserCreateRequest request);
    UserResponse find(UUID id);
    List<UserResponse> findAll();
    UserResponse update(UserUpdateRequest request);
    void delete(UUID id);
}

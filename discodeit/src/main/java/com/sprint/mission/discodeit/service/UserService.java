// service/UserService.java
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest request);
    Optional<UserResponse> getUserById(UUID id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UserUpdateRequest request);
    void deleteUser(UUID id);
}

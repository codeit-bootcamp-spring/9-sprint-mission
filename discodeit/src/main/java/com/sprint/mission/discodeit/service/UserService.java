package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;

import java.util.*;

public interface UserService {
    User create(CreateUserRequest request, UUID profileImageId);

    void remove(UUID id);

    UserResponse findByID(UUID id);

    List<UserResponse> findAll();

    User update(UUID id, UpdateUserRequest request, UUID newProfileImageId);

    User updateName(UUID id, String newName);

    User updatePassword(UUID id, String newPassword);

    User updateEmail(UUID id, String newEmail);
}

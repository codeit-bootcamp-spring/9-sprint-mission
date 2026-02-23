package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;

import java.util.*;

public interface UserService {
    User create(UserCreateRequest request, UUID profileImageId);

    void remove(UUID id);

    UserDto findByID(UUID id);

    List<UserDto> findAll();

    User update(UUID id, UserUpdateRequest request, UUID newProfileImageId);

    User updateName(UUID id, String newName);

    User updatePassword(UUID id, String newPassword);

    User updateEmail(UUID id, String newEmail);
}

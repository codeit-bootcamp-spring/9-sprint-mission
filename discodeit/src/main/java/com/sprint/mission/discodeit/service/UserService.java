package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserService.FindUserResponse;
import com.sprint.mission.discodeit.DTO.UserService.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.DTO.UserService.CreateUserRequest;

import java.util.*;

public interface UserService {
    User create(CreateUserRequest createUserRequest);

    void remove(UUID id);

    FindUserResponse findByID(UUID id);

    List<FindUserResponse> getAll();

    User update(UpdateUserRequest updateUserRequest);

    User updateName(UUID id, String newName);

    User updatePassword(UUID id, String newPassword);

    User updateEmail(UUID id, String newEmail);
}

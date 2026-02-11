package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserService.Response.FindUserResponse;
import com.sprint.mission.discodeit.DTO.UserService.Request.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.DTO.UserService.Request.CreateUserRequest;

import java.util.*;

public interface UserService {
    User create(CreateUserRequest request);

    void remove(UUID id);

    FindUserResponse findByID(UUID id);

    List<FindUserResponse> findAll();

    User update(UpdateUserRequest request);

    User updateName(UUID id, String newName);

    User updatePassword(UUID id, String newPassword);

    User updateEmail(UUID id, String newEmail);
}

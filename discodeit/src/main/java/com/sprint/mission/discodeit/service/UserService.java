package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);
    UserResponse find(UUID userId);
    List<UserResponse> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPassword, UUID newProfileId);
    void delete(UUID userId);
}

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDeleteRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserView;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserView create(UserCreateRequest request);
    UserView update(UserUpdateRequest request);
    UserView findById(UUID userId);
    List<UserView> findAll();
    void delete(UserDeleteRequest request);

    boolean existsById(UUID userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}

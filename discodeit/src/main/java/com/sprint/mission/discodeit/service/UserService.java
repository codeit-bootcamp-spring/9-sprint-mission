package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest request, Optional<BinaryContentCreateRequest> profileRequest);
    UserStatusResponse find(UUID userId);
    List<UserStatusResponse> findAll();
    User update(UUID userId, UserUpdateRequest request);
    void delete(UUID userId);


}

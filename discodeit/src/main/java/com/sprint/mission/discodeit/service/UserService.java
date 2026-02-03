package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.DTO.User.UserCreatRequest;
import com.sprint.mission.discodeit.service.DTO.User.UserResponse;
import com.sprint.mission.discodeit.service.DTO.User.UserUpdateRequest;

import java.util.List;
import java.util.UUID;


public interface UserService {
    User create(UserCreatRequest request);
    UserResponse find(UUID userId);
    List<UserResponse> findAll();
    UserResponse update(UserUpdateRequest request);
    void delete(UUID userId);




}

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.DTO.LoginRequest;
import com.sprint.mission.discodeit.service.DTO.User.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}

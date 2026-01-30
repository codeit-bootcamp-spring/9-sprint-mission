package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.DTO.LoginRequest;
import com.sprint.mission.discodeit.service.DTO.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}

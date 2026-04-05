package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;

public interface AuthService {

  UserResponse login(LoginRequest loginRequest);
}

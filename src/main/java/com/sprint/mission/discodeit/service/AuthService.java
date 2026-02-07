package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserLoginRequest;
import com.sprint.mission.discodeit.dto.user.UserLoginResponse;

public interface AuthService {
    UserLoginResponse login(UserLoginRequest request);
}

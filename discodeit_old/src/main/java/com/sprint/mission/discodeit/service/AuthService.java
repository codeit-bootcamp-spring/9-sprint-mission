package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.AuthLoginResponse;

public interface AuthService {
    AuthLoginResponse login(AuthLoginRequest request);
}


package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthDto;
import com.sprint.mission.discodeit.dto.UserDto;

public interface AuthService {
    UserDto.Response login(AuthDto.LoginRequest request);
}
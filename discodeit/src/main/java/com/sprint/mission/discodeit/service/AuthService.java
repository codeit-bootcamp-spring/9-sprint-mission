package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.AuthService.LoginRequest;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    public User Login(LoginRequest loginRequest);
}

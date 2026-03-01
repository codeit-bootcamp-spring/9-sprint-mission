package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.data.UserDto;
import com.sprint.mission.discordit.dto.request.LoginRequest;

public interface AuthService {

  UserDto login(LoginRequest loginRequest);
}

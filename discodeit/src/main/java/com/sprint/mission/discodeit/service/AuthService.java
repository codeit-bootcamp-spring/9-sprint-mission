package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.DTO.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {

    User login(LoginRequest loginRequest);
}

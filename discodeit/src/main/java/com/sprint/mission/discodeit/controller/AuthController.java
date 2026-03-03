package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController implements AuthApi {

  private final AuthService authService;

  @Override
  public ResponseEntity<String> login(LoginRequest loginRequest) {

    User user = authService.login(loginRequest);

    return ResponseEntity.ok(
        "login 성공 사용자: " + user.getUsername()
    );
  }
}
package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final UserMapper mapper;
  private final AuthService authService;

  @Override
  @PostMapping("/api/auth/login")
  public ResponseEntity<UserDto> login(
      @RequestBody @Valid LoginRequest loginRequest
  ) {
    UserDto loginUser = authService.login(loginRequest);

    return ResponseEntity.status(HttpStatus.OK).body(loginUser);

  }
}

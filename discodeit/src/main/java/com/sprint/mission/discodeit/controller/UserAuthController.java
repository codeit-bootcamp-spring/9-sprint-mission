package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserAuthApi;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController implements UserAuthApi {

  private final AuthService authService;
  private final UserService userService;
  private final UserStatusRepository userStatusRepository;

  @PostMapping(
      path = "/login"
  )
  @Override
  public ResponseEntity<UserDto> login(
      @RequestBody LoginRequest loginRequest) {
    try {
      User user = authService.login(loginRequest);
      Instant now = Instant.now();
      userStatusRepository.findByUserId(user.getId())
          .ifPresentOrElse(
              status -> {
                status.update(now);
                userStatusRepository.save(status);
              },
              () -> {
                UserStatus newStatus = new UserStatus(user.getId(), now);
                userStatusRepository.save(newStatus);
              }
          );
      return ResponseEntity.ok(new UserDto(
          user.getId(),
          user.getCreatedAt(),
          user.getUpdatedAt(),
          user.getUsername(),
          user.getEmail(),
          user.getProfileId(),
          true
      ));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping(
      path = "/logout")
  @Override
  public ResponseEntity<Void> logout() {

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
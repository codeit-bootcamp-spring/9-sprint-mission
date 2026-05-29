package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    log.info("내 정보 조회 요청: username={}", username);
    UserDto user = userService.findByUsername(username);
    return ResponseEntity.ok(user);
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@RequestBody @Valid UserRoleUpdateRequest request) {
    log.info("권한 수정 요청: userId={}, role={}", request.userId(), request.role());
    UserDto user = userService.updateRole(request);
    return ResponseEntity.ok(user);
  }
}

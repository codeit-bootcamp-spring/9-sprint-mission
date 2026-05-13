package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
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

  @GetMapping("csrf-token")
  @Override
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity
        .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .build();
  }

  @GetMapping("me")
  @Override
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UserDto user = userDetails.getUserDto();
    log.debug("현재 사용자 조회 응답: {}", user);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }

  @PutMapping("role")
  @Override
  public ResponseEntity<UserDto> updateRole(
      @RequestBody @Valid UserRoleUpdateRequest request) {
    UserDto updatedUser = userService.updateRole(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }
}

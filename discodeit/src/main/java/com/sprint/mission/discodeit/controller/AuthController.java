package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      log.warn("인증 정보가 없습니다.");
      return ResponseEntity.status(401).build();
    }
    log.debug("현재 인증된 사용자 조회: {}", userDetails.getUsername());
    UserDto userDto = userService.findByUsername(userDetails.getUsername());

    return ResponseEntity.ok(userDto);
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
    UserDto response = userService.updateRole(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/csrf-token")
  public CsrfToken getCsrfToken(HttpServletRequest request) {
    return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
  }
}
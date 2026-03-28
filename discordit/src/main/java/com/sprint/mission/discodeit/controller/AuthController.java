package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RequestLoginDto;
import com.sprint.mission.discodeit.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<String> loginUser(
      @RequestBody RequestLoginDto loginDto
  ) {
    authService.login(loginDto);
    return ResponseEntity.ok(loginDto.username() + " is login successfully");
  }

  @RequestMapping(value = "/logout", method = RequestMethod.POST)
  public ResponseEntity<String> loginUser() {
    authService.logout();
    return ResponseEntity.ok("Logout successfully");
  }
}
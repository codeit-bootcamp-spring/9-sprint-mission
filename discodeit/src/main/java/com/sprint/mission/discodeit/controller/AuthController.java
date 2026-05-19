package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

  @Override
  @GetMapping(path = "/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    ResponseCookie csrfCookie = ResponseCookie.from("XSRF-TOKEN", tokenValue)
        .path("/")
        .httpOnly(false)
        .sameSite("Lax")
        .build();

    return ResponseEntity
        .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .header(HttpHeaders.SET_COOKIE, csrfCookie.toString())
        .build();
  }

  @Override
  @GetMapping(path = "/me")
  public ResponseEntity<UserResponse> me(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDetails.getUserDto());
  }

  @Override
  @PutMapping(path = "/role")
  public ResponseEntity<UserResponse> updateRole(
      @Valid @RequestBody UserRoleUpdateRequest request
  ) {
    UserResponse updatedUser = userService.updateRole(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }
}

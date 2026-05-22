package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  @GetMapping(path = "/csrf-token")
  public ResponseEntity<Void> getCsrfToken() {
    return ResponseEntity
        .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .build();
  }

  @Override
  @PostMapping(path = "/refresh")
  public ResponseEntity<JwtDto> refresh(
      @CookieValue(name = JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME) String refreshToken
  ) {
    String username = jwtTokenProvider.getUsername(refreshToken);
    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);
    String accessToken = jwtTokenProvider.refreshToken(userDetails);

    JwtDto body = new JwtDto(
        userDetails.getUserDto(),
        accessToken,
        "Bearer",
        Instant.now()
            .plusSeconds(jwtTokenProvider.getExpirationSeconds())
            .toString()
    );

    return ResponseEntity
        .status(HttpStatus.OK)
        .header("Authorization", "Bearer " + accessToken)
        .body(body);
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

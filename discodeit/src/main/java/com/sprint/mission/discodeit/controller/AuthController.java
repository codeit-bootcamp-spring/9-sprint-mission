package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
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
  private final JwtRegistry jwtRegistry;

  @GetMapping("csrf-token")
  @Override
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity
        .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .build();
  }

  @PostMapping("refresh")
  @Override
  public ResponseEntity<?> refresh(
      @CookieValue(value = JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, required = false)
      String refreshToken,
      HttpServletResponse response
  ) {
    if (!jwtTokenProvider.validateRefreshToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      IllegalArgumentException exception = new IllegalArgumentException("Invalid refresh token");
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse(exception, HttpStatus.UNAUTHORIZED.value()));
    }

    UserDto user = userService.find(jwtTokenProvider.getUserId(refreshToken));
    String accessToken = jwtTokenProvider.generateAccessToken(user);
    String rotatedRefreshToken = jwtTokenProvider.generateRefreshToken(user);
    jwtRegistry.rotateJwtInformation(refreshToken, accessToken, rotatedRefreshToken);
    response.addCookie(createRefreshTokenCookie(rotatedRefreshToken));

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(new JwtDto(user, accessToken));
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

  private Cookie createRefreshTokenCookie(String refreshToken) {
    Cookie refreshTokenCookie =
        new Cookie(JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    return refreshTokenCookie;
  }
}

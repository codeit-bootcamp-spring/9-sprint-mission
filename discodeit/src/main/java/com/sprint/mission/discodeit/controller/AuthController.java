package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.web.csrf.CsrfToken;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @GetMapping("csrf-token")
  @PreAuthorize("isAnonymous() or isAuthenticated()")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청: {}", csrfToken.getToken());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UserDto userDto = userService.find(userDetails.getUserDto().id());
    return ResponseEntity.ok(userDto);
  }

  @PutMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request) {
    UserDto userDto = userService.updateRole(request);
    return ResponseEntity.ok(userDto);
  }

  @PostMapping("refresh")
  @PreAuthorize("isAnonymous() or isAuthenticated()")
  public ResponseEntity<JwtDto> refresh(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = CookieUtils.getCookieValue(request, REFRESH_TOKEN_COOKIE)
        .orElse(null);

    if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = jwtTokenProvider.getUsername(refreshToken);
    UUID userId = jwtTokenProvider.getUserId(refreshToken);
    String role = jwtTokenProvider.getRole(refreshToken);

    String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, role);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, username, role);

    // Redis에서 토큰 교체
    UserDto userDto = userService.find(userId);
    JwtInformation newJwtInformation = new JwtInformation(userDto, newAccessToken, newRefreshToken);
    jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

    Cookie newRefreshCookie = new Cookie(REFRESH_TOKEN_COOKIE, newRefreshToken);
    newRefreshCookie.setHttpOnly(true);
    newRefreshCookie.setPath("/");
    response.addCookie(newRefreshCookie);

    return ResponseEntity.ok(new JwtDto(userDto, newAccessToken));
  }
}
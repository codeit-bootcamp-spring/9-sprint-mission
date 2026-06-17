package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping("csrf-token")
  @PreAuthorize("isAnonymous() or isAuthenticated()")  // 누구나 접근 가능
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

  // ResponseEntity<?> → ResponseEntity<JwtDto>로 구체적 명시
  @PostMapping("refresh")
  @PreAuthorize("isAnonymous() or isAuthenticated()")
  public ResponseEntity<JwtDto> refresh(HttpServletRequest request, HttpServletResponse response) {
    // 쿠키 추출 로직 → CookieUtils 분리
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

    Cookie newRefreshCookie = new Cookie(REFRESH_TOKEN_COOKIE, newRefreshToken);
    newRefreshCookie.setHttpOnly(true);
    newRefreshCookie.setPath("/");
    response.addCookie(newRefreshCookie);

    UserDto userDto = userService.find(userId);
    return ResponseEntity.ok(new JwtDto(userDto, newAccessToken));
  }
}
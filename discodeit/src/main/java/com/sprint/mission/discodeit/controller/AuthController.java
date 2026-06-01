package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Arrays;
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

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청: {}", csrfToken.getToken());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  //  토큰의 캐시된 정보 말고 DB에서 최신 정보 재조회
  @GetMapping("me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UserDto userDto = userService.find(userDetails.getUserDto().id());
    return ResponseEntity.ok(userDto);
  }

  //  Path 설정 대신 어노테이션으로 ADMIN 권한 명시
  @PutMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request) {
    UserDto userDto = userService.updateRole(request);
    return ResponseEntity.ok(userDto);
  }

  @PostMapping("refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = null;
    if (request.getCookies() != null) {
      refreshToken = Arrays.stream(request.getCookies())
          .filter(c -> "REFRESH_TOKEN".equals(c.getName()))
          .map(Cookie::getValue)
          .findFirst()
          .orElse(null);
    }

    if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse(
              Instant.now(),
              "INVALID_REFRESH_TOKEN",
              "유효하지 않은 리프레시 토큰입니다.",
              Map.of(),
              "TokenException",
              HttpStatus.UNAUTHORIZED.value()
          ));
    }

    String username = jwtTokenProvider.getUsername(refreshToken);
    UUID userId = jwtTokenProvider.getUserId(refreshToken);
    String role = jwtTokenProvider.getRole(refreshToken);

    String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, role);

    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, username, role);
    Cookie newRefreshCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
    newRefreshCookie.setHttpOnly(true);
    newRefreshCookie.setPath("/");
    response.addCookie(newRefreshCookie);

    // DB에서 최신 유저 정보 재조회
    UserDto userDto = userService.find(userId);

    return ResponseEntity.ok(new JwtDto(userDto, newAccessToken));
  }
}
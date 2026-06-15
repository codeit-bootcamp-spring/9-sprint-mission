package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.secure.JwtRegistry;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserService userService;
  private final JwtRegistry jwtRegistry;

  @Override
  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @PutMapping("role")
  public ResponseEntity<UserDto> updateRole(
      @RequestBody UserRoleUpdateRequest userRoleUpdateRequest) {
    UUID userId = userRoleUpdateRequest.userId();
    UserRole role = userRoleUpdateRequest.role();
    log.info("유저 권한 업데이트 요청: userId={}, role={}", userId, role);

    UserDto userDto = userService.updateRole(userId, role);
    authService.deleteSession(userId);
    jwtRegistry.invalidateJwtInformationByUserId(userId);

    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @PostMapping("refresh")
  public ResponseEntity<JwtDto> refreshToken(
      @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse response) {
    if (refreshToken == null) {
      return ResponseEntity.ok(JwtDto.builder().build());
    }

    try {
      JwtDto jwtDto = authService.refresh(refreshToken);

      Cookie newRefreshTokenCookie = new Cookie("REFRESH_TOKEN", jwtDto.refreshToken());
      newRefreshTokenCookie.setHttpOnly(true);
      newRefreshTokenCookie.setPath("/");
      response.addCookie(newRefreshTokenCookie);

      return ResponseEntity.ok(jwtDto);
    } catch (Exception e) {
      log.warn("Refresh 실패 (세션 없음 또는 만료): {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

}

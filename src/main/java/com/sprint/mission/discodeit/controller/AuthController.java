package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.RefreshTokenService;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping("me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    log.info("내 정보 조회 요청");
    UUID userId = userDetails.getUserDto().id();
    UserDto userDto = userService.find(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  @PutMapping("role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    log.info("권한 수정 요청");
    UserDto userDto = authService.updateRole(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  /**
   * 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
   */
  @PostMapping("refresh")
  public ResponseEntity<Object> refresh(
      @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse response
  ) {
    log.info("토큰 갱신 요청");

    if (refreshToken == null || refreshToken.isBlank()) {
      log.warn("리프레시 토큰 쿠키가 없습니다.");
      DiscodeitException ex = new DiscodeitException(ErrorCode.INVALID_REFRESH_TOKEN);
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse(ex, HttpStatus.UNAUTHORIZED.value()));
    }

    // 토큰 검증
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      log.warn("유효하지 않은 리프레시 토큰");
      DiscodeitException ex = new DiscodeitException(ErrorCode.INVALID_TOKEN);
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse(ex, HttpStatus.UNAUTHORIZED.value()));
    }

    // 리프레시 토큰 확인
    if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
      log.warn("액세스 토큰으로 갱신 요청함");
      DiscodeitException ex = new DiscodeitException(ErrorCode.INVALID_REFRESH_TOKEN);
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse(ex, HttpStatus.BAD_REQUEST.value()));
    }

    try {
      // 토큰에서 사용자 ID와 jti 추출
      UUID userId = jwtTokenProvider.getUserId(refreshToken);
      UUID oldJti = jwtTokenProvider.getJti(refreshToken);
      UserDto userDto = userService.find(userId);

      // rotate: validate server-side and issue new refresh token
      String newRefreshToken = refreshTokenService.rotateRefreshToken(oldJti, userId);

      // 새로운 액세스 토큰 생성
      String newAccessToken = jwtTokenProvider.generateAccessToken(userDto);

      Cookie refreshCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(true); // HTTPS only
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge((int) jwtTokenProvider.getRefreshTokenValiditySeconds());
      refreshCookie.setAttribute("SameSite", "Strict"); // CSRF protection
      response.addCookie(refreshCookie);

      log.info("토큰 갱신 완료 - userId: {}", userId);
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(new JwtDto(newAccessToken, "Bearer"));

    } catch (Exception e) {
      log.error("토큰 갱신 중 오류 발생", e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null);
    }
  }
}

package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
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

  private final AuthService authService;
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${jwt.refresh-token-expiry}")
  private long refreshTokenExpiry;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("refresh")
  public ResponseEntity<JwtDto> refresh(HttpServletRequest request, HttpServletResponse response) {
    log.info("토큰 재발급 요청");
    String refreshToken = request.getCookies() == null ? null :
        Arrays.stream(request.getCookies())
            .filter(c -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(null);

    if (refreshToken == null) {
      throw new InvalidTokenException();
    }

    try {
      var claims = jwtTokenProvider.validateAndGetClaims(refreshToken);
      UUID userId = jwtTokenProvider.extractUserId(claims);
      UserDto userDto = userService.find(userId);

      String newAccessToken = jwtTokenProvider.generateAccessToken(userDto);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDto);

      Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, newRefreshToken);
      cookie.setHttpOnly(true);
      cookie.setPath("/");
      cookie.setMaxAge((int) (refreshTokenExpiry / 1000));
      response.addCookie(cookie);

      return ResponseEntity.ok(new JwtDto(userDto, newAccessToken));
    } catch (InvalidTokenException e) {
      throw e;
    } catch (Exception e) {
      throw new InvalidTokenException(e);
    }
  }

  @PutMapping("role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    log.info("권한 수정 요청");
    UserDto userDto = authService.updateRole(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }
}

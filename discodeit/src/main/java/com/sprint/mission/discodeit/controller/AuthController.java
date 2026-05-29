package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

  private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 14 * 24 * 60 * 60;

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("refresh")
  public ResponseEntity<?> refresh(
      @CookieValue(name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
      HttpServletResponse response
  ) {
    if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      return unauthorizedRefreshTokenResponse();
    }

    try {
      String username = jwtTokenProvider.getUserIdFromToken(refreshToken);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
      );

      String accessToken = jwtTokenProvider.createAccessToken(authentication);
      String rotatedRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

      if (userDetails instanceof DiscodeitUserDetails discodeitUserDetails) {
        UserDto userDto = discodeitUserDetails.getUserDto();
        jwtRegistry.rotateJwtInformation(refreshToken, new JwtInformation(
            userDto,
            accessToken,
            rotatedRefreshToken,
            jwtTokenProvider.getExpirationTime(rotatedRefreshToken)
        ));
        response.addCookie(createRefreshTokenCookie(rotatedRefreshToken));
        return ResponseEntity.ok(new JwtDto(userDto, accessToken));
      }

      return unauthorizedRefreshTokenResponse();
    } catch (RuntimeException exception) {
      return unauthorizedRefreshTokenResponse();
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

  private ResponseEntity<ErrorResponse> unauthorizedRefreshTokenResponse() {
    ErrorResponse errorResponse = new ErrorResponse(
        new RuntimeException("Invalid refresh token"),
        HttpServletResponse.SC_UNAUTHORIZED
    );
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  private Cookie createRefreshTokenCookie(String refreshToken) {
    Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
    return cookie;
  }
}

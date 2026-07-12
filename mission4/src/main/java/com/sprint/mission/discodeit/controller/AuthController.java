package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @GetMapping("/api/auth/csrf-token")
  public ResponseEntity<Void> getCsrfToken(HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    if (csrfToken != null) {
      log.debug("CSRF 토큰 발급 성공: {}", csrfToken.getToken());

      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    log.error("필터에서 CSRF 토큰을 생성하지 못했습니다.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @GetMapping("/api/auth/me")
  public ResponseEntity<UserDto> getCurrentUser(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UserDto dto = UserDto.builder().
        id(userDetails.getId())
        .username(userDetails.getUsername())
        .email(userDetails.getEmail())
        .online(true)
        .build();
    return ResponseEntity.ok(dto);
  }


  @PutMapping("/api/auth/role")
  public ResponseEntity<UserDto> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
    UserDto dto = userService.updateRole(request);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/api/auth/refresh")
  public ResponseEntity<?> refresh(
      @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse response) {
    if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      log.warn("[Token Refresh] 유효하지 않거나 무효화된 리프레시 토큰 접근");
      Map<String, String> errorBody = new java.util.HashMap<>();
      errorBody.put("error", "Unauthorized");
      errorBody.put("message", "리프레시 토큰이 유효하지 않습니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(errorBody);
    }
    Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
    String username = authentication.getName();

    String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);
    LocalDateTime accessExpires = jwtTokenProvider.getExpiration(newAccessToken);
    LocalDateTime refreshExpires = jwtTokenProvider.getExpiration(newRefreshToken);

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UserDto userDto = UserDto.builder()
        .id(((DiscodeitUserDetails) userDetails).getId())
        .username(userDetails.getUsername())
        .email(((DiscodeitUserDetails) userDetails).getEmail())
        .online(true)
        .build();

    JwtInformation newJwtInfo = new JwtInformation(
        userDto, newAccessToken, newRefreshToken, accessExpires, refreshExpires
    );
    jwtRegistry.rotateJwtInformation(refreshToken, newJwtInfo);

    ResponseCookie newRefreshCookie = ResponseCookie.from("REFRESH_TOKEN", newRefreshToken)
        .httpOnly(true)
        .secure(false) // 개발 환경 고려, 운영 환경에서는 true 권장
        .path("/")
        .sameSite("Lax")
        .maxAge(60 * 60 * 24 * 7)
        .build();
    response.addHeader("Set-Cookie", newRefreshCookie.toString());

    JwtDto jwtDto = new JwtDto(newAccessToken, userDto, newRefreshToken);

    log.info("[Token Refresh] 유저 [{}] 토큰 재발급 및 RTR 쿠키 갱신 완료", username);
    return ResponseEntity.ok(jwtDto);

  }


}

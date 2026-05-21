package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
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

  @GetMapping("/api/auth/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰이 성공적으로 생성되었습니다: {}", tokenValue);

    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
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
    if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
      log.warn("[Token Refresh] 유효하지 않거나 존재하지 않는 리프레시 토큰 접근");
      Map<String, String> errorBody = new java.util.HashMap<>();
      errorBody.put("error", "Unauthorized");
      errorBody.put("message", "리프레시 토큰이 유효하지 않습니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(errorBody);
    }
    Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
    String username = authentication.getName();

    String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
    String newRefreshToken = "mock-refresh-token-" + username + "-rotated";
    Cookie newRefreshCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
    newRefreshCookie.setHttpOnly(true);
    newRefreshCookie.setPath("/");
    newRefreshCookie.setMaxAge(60 * 60 * 24 * 7);
    response.addCookie(newRefreshCookie);

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UserDto userDto = UserDto.builder()
        .id(((DiscodeitUserDetails) userDetails).getId())
        .username(userDetails.getUsername())
        .email(((DiscodeitUserDetails) userDetails).getEmail())
        .online(true)
        .build();

    JwtDto jwtDto = new JwtDto(newAccessToken, userDto);

    log.info("[Token Refresh] 유저 [{}] 토큰 재발급 및 RTR 쿠키 갱신 완료", username);
    return ResponseEntity.ok(jwtDto);

  }


}

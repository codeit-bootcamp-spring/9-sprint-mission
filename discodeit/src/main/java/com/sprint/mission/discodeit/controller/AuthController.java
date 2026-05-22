package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(
      @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse response) {

    if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
      log.warn("유효하지 않거나 만료된 리프레시 토큰으로 접근을 시도했습니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "유효하지 않거나 만료된 리프레시 토큰입니다."));
    }

    try {
      JWTClaimsSet claimsSet = jwtTokenProvider.getClaims(refreshToken);
      String username = claimsSet.getSubject();

      UserDetails userDetails = new User(username, "",
          Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
      Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
          userDetails.getAuthorities());

      String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
      String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

      Cookie newRefreshTokenCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
      newRefreshTokenCookie.setHttpOnly(true);
      newRefreshTokenCookie.setSecure(true);
      newRefreshTokenCookie.setPath("/");
      newRefreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
      response.addCookie(newRefreshTokenCookie);

      UserDto userDto = userService.findByUsername(username);

      log.debug("리프레시 토큰을 통해 토큰 재발급 성공: {}", username);
      return ResponseEntity.ok(new JwtDto(userDto, newAccessToken));

    } catch (ParseException e) {
      log.error("리프레시 토큰 파싱 중 오류가 발생했습니다.", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "토큰 파싱 중 오류가 발생했습니다."));
    }
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
    UserDto response = userService.updateRole(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/csrf-token")
  public CsrfToken getCsrfToken(HttpServletRequest request) {
    return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
  }
}
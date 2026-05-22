package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.RefreshTokenResultDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.exception.ErrorResponse;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.exception.security.FailUpdateRefreshTokenException;
import com.sprint.mission.discodeit.exception.security.NotExistRefreshTokenException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        if (userDetails == null) {
            log.warn("사용자 기본 정보 조회 요청: 인증되지 않은 사용자 (세션 없음)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("내 정보 조회 요청: username={}", userDetails.getUsername());

        UserDto userDto = userDetails.getUserDto();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request) {
        log.info("사용자 권한 변경 요청: username={}, newRole={}", request.userId(), request.newRole());
        UserDto updatedUser = userService.updateRole(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
        @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
        HttpServletResponse response
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(new NotExistRefreshTokenException(), HttpStatus.UNAUTHORIZED.value()));
        }

        try {
            RefreshTokenResultDto result = authService.refreshAccessToken(refreshToken);

            Cookie newRefreshCookie = new Cookie("REFRESH_TOKEN", result.newRefreshToken());
            newRefreshCookie.setHttpOnly(true);
            newRefreshCookie.setPath("/api/auth");
            newRefreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일

            response.addCookie(newRefreshCookie);

            return ResponseEntity.ok(result.jwtDto());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(new FailUpdateRefreshTokenException(), HttpStatus.UNAUTHORIZED.value()));
        }
    }
}

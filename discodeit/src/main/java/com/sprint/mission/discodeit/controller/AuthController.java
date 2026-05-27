package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken; // 추가된 import
import org.springframework.web.bind.annotation.GetMapping; // 추가된 import
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserService userService;

  @PostMapping(path = "login")
  @Override // 인터페이스 메서드를 구현한다는 것을 명시
  public ResponseEntity<UserDto> login(@ModelAttribute LoginRequest loginRequest) {
    // 🔍 [매우 중요] 실제로 값이 들어오는지 로그로 확인합니다.
    log.info("로그인 요청 내부 데이터 확인: username=[{}], password=[{}]",
        loginRequest.username(), loginRequest.password());

    UserDto user = authService.login(loginRequest);
    log.debug("로그인 응답: {}", user);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }

  @GetMapping(path = "csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping(path = "me")
  public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    log.debug("현재 사용자 정보 조회 요청 수신");

    if (userDetails == null) {
      log.warn("인증되지 않은 사용자의 접근 시도");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    UserDto userDto = userDetails.getUserDto();
    log.info("현재 사용자 정보 조회 완료: username={}", userDto.username());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  @PutMapping(path = "role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    UserDto updatedUser = userService.updateRole(request);
    return ResponseEntity.ok(updatedUser);
  }
}

package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final AuthService authService;

  @Override
  public ResponseEntity<UserDto> login(LoginRequest request, HttpSession session) {
    // 서비스 레이어에서 유저 검증 및 DTO 반환
    UserDto userDto = authService.login(request);

    // 인증 성공 시 세션에 유저 ID 저장
    session.setAttribute("USER_ID", userDto.id());

    return ResponseEntity.ok(userDto);
  }
}
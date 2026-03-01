package com.sprint.mission.discordit.controller;

import com.sprint.mission.discordit.dto.data.UserDto;
import com.sprint.mission.discordit.dto.request.LoginRequest;
import com.sprint.mission.discordit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 API", description = "로그인 및 회원가입 관련 기능을 제공합니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인", description = "아이디와 비밀번호를 받아 로그인을 진행합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패 (아이디/비밀번호 불일치)", content = @Content)
  })
  @RequestMapping(path = "login", method = RequestMethod.POST)
  public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
    UserDto login = authService.login(loginRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(login);
  }
}

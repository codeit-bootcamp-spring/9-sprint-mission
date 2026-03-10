package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관리 API")
public interface AuthApi {

  @Operation(summary = "로그인 및 세션 발급")
  @PostMapping("/login")
  ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest request, HttpSession session);
}
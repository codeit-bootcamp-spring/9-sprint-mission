package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponse(responseCode = "203", description = "CSRF 토큰 발급 성공")
  ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

  @Operation(summary = "액세스 토큰 재발급")
  @ApiResponse(responseCode = "200", description = "액세스 토큰 재발급 성공")
  @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
  ResponseEntity<?> refresh(String refreshToken, HttpServletResponse response);

  @Operation(summary = "사용자 권한 수정")
  @ApiResponse(responseCode = "200", description = "사용자 권한 수정 성공")
  ResponseEntity<UserDto> updateRole(@RequestBody @Valid UserRoleUpdateRequest request);
}

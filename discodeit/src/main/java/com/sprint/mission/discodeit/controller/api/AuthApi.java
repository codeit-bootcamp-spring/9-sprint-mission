package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "203", description = "CSRF 토큰 발급 성공 (쿠키로 전달)"),
      @ApiResponse(responseCode = "400", description = "CSRF 토큰 요청 실패")
  })
  ResponseEntity<Void> getCsrfToken(
      @Parameter(hidden = true) CsrfToken csrfToken);


  @Operation(summary = "유져 권한 업데이트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "권한 업데이트 성공"),
      @ApiResponse(responseCode = "403", description = "업데이트 권한 없음")
  })
  ResponseEntity<UserDto> updateRole(
      @Parameter UserRoleUpdateRequest userRoleUpdateRequest
  );

  @Operation(summary = "refresh 토큰을 활용해 엑세스 토큰 재발급")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = JwtDto.class))),
      @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
  })
  ResponseEntity<JwtDto> refreshToken(
      @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse response);
}
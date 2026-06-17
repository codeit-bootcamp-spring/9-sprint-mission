package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
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

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 요청")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "CSRF 토큰 요청 성공"),
      @ApiResponse(responseCode = "400", description = "CSRF 토큰 요청 실패")
  })
  ResponseEntity<Void> getCsrfToken(
      @Parameter(hidden = true) CsrfToken csrfToken
  );


  @Operation(summary = "사용자 권한 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "권한 변경 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      )
  })
  ResponseEntity<UserDto> updateRole(
      @Parameter(description = "권한 수정 요청 정보") RoleUpdateRequest request);

  @Operation(summary = "토큰 리프레시")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "토큰 리프레시 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
  })
  ResponseEntity<JwtDto> refresh(
      @Parameter(description = "리프레시 토큰") String refreshToken,
      @Parameter(hidden = true) HttpServletResponse response
  );
} 
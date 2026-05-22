package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 호환 엔드포인트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "203", description = "토큰 기반 인증에서 별도 CSRF 토큰 없이 성공")
  })
  ResponseEntity<Void> getCsrfToken();

  @Operation(summary = "Access Token 재발급")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공")
  })
  ResponseEntity<JwtDto> refresh(String refreshToken);

  @Operation(summary = "사용자 권한 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "사용자 권한 수정 성공",
          content = @Content(schema = @Schema(implementation = UserResponse.class))
      )
  })
  ResponseEntity<UserResponse> updateRole(
      @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UserRoleUpdateRequest.class)))
      @Valid UserRoleUpdateRequest request
  );
}

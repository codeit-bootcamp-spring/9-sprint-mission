package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "CSRF 토큰 발급 성공")
  })
  ResponseEntity<Void> getCsrfToken(
      @Parameter(hidden = true)
      CsrfToken csrfToken
  );

  @Operation(summary = "현재 사용자 정보 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "현재 사용자 정보 조회 성공",
          content = @Content(schema = @Schema(implementation = UserResponse.class))
      )
  })
  ResponseEntity<UserResponse> me(
      @Parameter(hidden = true)
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

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

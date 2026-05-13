package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "로그인")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = UserResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with username {username} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "비밀번호가 일치하지 않음",
          content = @Content(examples = @ExampleObject(value = "Wrong password"))
      )
  })
  ResponseEntity<UserResponse> login(
      @Parameter(description = "로그인 정보")
      @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoginRequest.class)))
      LoginRequest loginRequest
  );

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "203", description = "CSRF 토큰 발급 성공")
  })
  ResponseEntity<Void> getCsrfToken(
      @Parameter(hidden = true)
      CsrfToken csrfToken
  );
}

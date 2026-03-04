package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증(로그인) API")
public interface AuthApi {

  @Operation(summary = "로그인")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = User.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 로그인 요청 데이터",
          content = @Content(examples = @ExampleObject(value = "Invalid login data"))
      ),
      @ApiResponse(
          responseCode = "401", description = "로그인 실패 (이메일 또는 비밀번호 불일치)",
          content = @Content(examples = @ExampleObject(value = "Invalid email or password"))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User not found"))
      )
  })
  ResponseEntity<User> login(
      @Parameter(
          description = "로그인 요청 정보 (이메일, 비밀번호)",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody LoginRequest loginRequest
  );
}

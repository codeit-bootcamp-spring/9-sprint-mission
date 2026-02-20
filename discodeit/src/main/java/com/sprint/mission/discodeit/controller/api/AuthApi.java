package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/auth")
public interface AuthApi {

  @Operation(
      summary = "로그인",
      description = "사용자 로그인을 수행합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(responseCode = "401", description = "로그인 실패")
  })
  @PostMapping("/login")
  ResponseEntity<String> login(

      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "로그인 요청 정보",
          required = true
      )
      @RequestBody LoginRequest loginRequest
  );
}
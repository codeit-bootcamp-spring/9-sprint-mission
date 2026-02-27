package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Auth", description = "Auth API")
@RequestMapping("/api/auth")
public interface AuthApi {

  @Operation(summary = "로그인")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = UserView.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(implementation = com.sprint.mission.discodeit.exception.ErrorResponse.class))
      )
  })
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<UserView> login(
      @Parameter(description = "로그인 요청")
      @RequestBody LoginRequest request
  );
}

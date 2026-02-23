package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface AuthApi {
  @Operation(summary = "BinaryContent 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Login 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      )
  })
  public ResponseEntity<User> Login(@Parameter(description = "Login 정보") LoginRequest request);
}

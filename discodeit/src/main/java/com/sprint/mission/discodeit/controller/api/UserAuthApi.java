package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "UserAuth", description = "UserAuth API")
public interface UserAuthApi {

  @Operation(summary = "User login")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "401", description = "로그인 실패",
          content = @Content(examples = @ExampleObject(value = "Login fail"))
      )
  })
  ResponseEntity<UserDto> login(
      @Parameter(description = "로그인 요청 정보")
      LoginRequest loginrequest);


  @Operation(summary = "User logout")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "로그아웃 성공"
      )
  })
  ResponseEntity<Void> logout();
}

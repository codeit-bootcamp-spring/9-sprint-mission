package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "203", description = "CSRF 토큰 발급 성공")
  })
  ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

  ResponseEntity<UserDto> getMe(DiscodeitUserDetails userDetails);

  @Operation(summary = "권한 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "권한 수정 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class)))
  })
  ResponseEntity<UserDto> updateRole(RoleUpdateRequest request);
}
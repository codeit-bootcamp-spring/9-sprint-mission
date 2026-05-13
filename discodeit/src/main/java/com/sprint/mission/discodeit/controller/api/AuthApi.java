package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponse(responseCode = "203", description = "CSRF 토큰 발급 성공")
  ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

  @Operation(summary = "현재 사용자 조회")
  @ApiResponse(responseCode = "200", description = "현재 사용자 조회 성공")
  ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails);
}

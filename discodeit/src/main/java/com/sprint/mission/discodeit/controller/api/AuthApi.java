package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @GetMapping("csrf-token")
  ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

  @Operation(summary = "현재 사용자 정보 조회", description = "세션 ID를 기반으로 현재 로그인된 사용자의 기본 정보를 반환합니다.")
  ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails);

  @PutMapping("role")
  ResponseEntity<UserDto> role(@RequestBody RoleUpdateRequest request);
}
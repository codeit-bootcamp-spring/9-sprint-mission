package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "내 정보 조회", description = "세션 기반으로 현재 로그인한 사용자 정보를 반환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
  })
  ResponseEntity<UserDto> me(UserDetails userDetails);

  @Operation(summary = "사용자 권한 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "권한 수정 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
  })
  ResponseEntity<UserDto> updateRole(UserRoleUpdateRequest request);
}

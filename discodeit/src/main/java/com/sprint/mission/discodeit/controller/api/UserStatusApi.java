package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusView;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "UserStatus", description = "UserStatus API")
@RequestMapping("/api/users/{userId}/userStatus")
public interface UserStatusApi {

  @Operation(summary = "사용자 온라인 상태 업데이트")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "업데이트 성공",
          content = @Content(schema = @Schema(implementation = UserStatusView.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  UserStatusView updateOnline(
      @Parameter(description = "User ID")
      @PathVariable UUID userId,
      @Parameter(description = "온라인 상태 업데이트 params")
      @RequestBody(required = false) UserStatusUpdateRequest.Params params
  );
}

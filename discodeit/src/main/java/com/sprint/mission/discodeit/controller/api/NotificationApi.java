package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notification", description = "Notification API")
public interface NotificationApi {

  @Operation(summary = "알림 조회", description = "인증된 사용자의 모든 알림 목록을 최신순으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "알림 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
      ),
      @ApiResponse(
          responseCode = "401", description = "인증되지 않은 요청 (유효하지 않거나 없는 토큰)",
          content = @Content(examples = @ExampleObject(value = "Full authentication is required to access this resource"))
      )
  })
  ResponseEntity<List<NotificationDto>> getNotifications(DiscodeitUserDetails userDetails);

  @Operation(summary = "알림 확인 (삭제)", description = "특정 알림을 확인하고 리스트에서 삭제 처리합니다. 본인의 알림만 수행할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "알림 확인 및 삭제 완료"
      ),
      @ApiResponse(
          responseCode = "401", description = "인증되지 않은 요청",
          content = @Content(examples = @ExampleObject(value = "Full authentication is required to access this resource"))
      ),
      @ApiResponse(
          responseCode = "403", description = "인가되지 않은 요청 (타인의 알림 삭제 시도)",
          content = @Content(examples = @ExampleObject(value = "본인의 알림만 확인할 수 있습니다."))
      ),
      @ApiResponse(
          responseCode = "404", description = "알림을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "해당 알림을 찾을 수 없습니다. ID: {notificationId}"))
      )
  })
  ResponseEntity<Void> checkNotification(
      @Parameter(description = "확인(삭제)할 알림 ID") UUID notificationId,
      DiscodeitUserDetails userDetails
  );
}
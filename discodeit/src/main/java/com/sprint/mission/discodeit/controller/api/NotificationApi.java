package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.secure.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Notification", description = "Notification API")
public interface NotificationApi {

  @Operation(summary = "Notification 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Notification 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
      ),
      @ApiResponse(
          responseCode = "401", description = "user 인증 실패",
          content = @Content(examples = @ExampleObject(value = "user 인증정보를 찾을 수 없음"))
      )
  })
  ResponseEntity<List<NotificationDto>> findAll(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

  @Operation(summary = "Notification 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Notification 알림 확인"
      ),
      @ApiResponse(
          responseCode = "401", description = "인증되지 않은 요청",
          content = @Content(examples = @ExampleObject(value = "user 인증정보를 찾을 수 없음"))
      ),
      @ApiResponse(
          responseCode = "403", description = "인가되지 않은 요청",
          content = @Content(examples = @ExampleObject(value = "요청자 본인의 알림만 수행할 수 있음"))
      ),
      @ApiResponse(
          responseCode = "404", description = "알림을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "알림을 찾을 수 없음"))
      )
  })
  ResponseEntity<Void> delete(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

}

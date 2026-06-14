package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

import java.util.List;
import java.util.UUID;

public interface NotificationApi {

  @Operation(summary = "알림 조회")
  @ApiResponse(responseCode = "200")
  ResponseEntity<List<NotificationDto>> findAllByReceiverId(DiscodeitUserDetails principal);

  @Operation(summary = "알림 삭제")
  @ApiResponse(responseCode = "204")
  ResponseEntity<Void> delete(UUID notificationId, DiscodeitUserDetails principal);
}



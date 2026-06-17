package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> findAllByReceiver(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    UUID receiverId = userDetails.getUserDto().id();
    log.debug("알림 조회 요청: receiverId={}", receiverId);

    List<NotificationDto> notifications = notificationService.findAllByReceiverId(receiverId);
    return ResponseEntity.ok(notifications);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    UUID currentUserId = userDetails.getUserDto().id();
    log.debug("알림 삭제 요청: notificationId={}, requesterId={}", notificationId, currentUserId);

    notificationService.delete(notificationId, currentUserId);
    return ResponseEntity.noContent().build();
  }
}

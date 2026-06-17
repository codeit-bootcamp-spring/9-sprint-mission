package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.basic.BasicNotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

  private final BasicNotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> getNotification(
      @AuthenticationPrincipal
      DiscodeitUserDetails userDetails) {
    List<NotificationDto> notifications = notificationService.getNotification(userDetails.getId());
    return ResponseEntity.ok(notifications);
  }

  @DeleteMapping("{notificationId}")
  public ResponseEntity<Void> deleteNotification(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    notificationService.deleteNotification(notificationId, userDetails.getId());
    return ResponseEntity.noContent().build();
  }

}

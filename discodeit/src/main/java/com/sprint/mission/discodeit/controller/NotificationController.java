package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> findAll(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UUID receiverId = userDetails.getUserDto().id();
    List<NotificationDto> notifications = notificationService.findAllByReceiverId(receiverId);
    return ResponseEntity.ok(notifications);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UUID requesterId = userDetails.getUserDto().id();
    notificationService.delete(notificationId, requesterId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}

package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  // GET /api/notifications — 내 알림 목록 조회
  @GetMapping
  public ResponseEntity<List<NotificationDto>> findAll(
      @AuthenticationPrincipal User principal) {
    log.info("알림 목록 조회 요청: userId={}", principal.getId());
    List<NotificationDto> notifications =
        notificationService.findAllByReceiverId(principal.getId());
    return ResponseEntity.ok(notifications);
  }

  // DELETE /api/notifications/{notificationId} — 알림 확인(삭제)
  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal User principal) {
    log.info("알림 삭제 요청: notificationId={}, requesterId={}", notificationId, principal.getId());
    notificationService.delete(notificationId, principal.getId());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}

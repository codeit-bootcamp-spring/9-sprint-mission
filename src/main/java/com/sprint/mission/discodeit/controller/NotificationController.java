package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> find(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UUID loginUserId = userDetails.getUserDto().id();
        List<NotificationDto> response = notificationService.getNotifications(loginUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> read(@AuthenticationPrincipal DiscodeitUserDetails userDetails, @PathVariable UUID notificationId) {
        UUID loginUserId = userDetails.getUserDto().id();
        notificationService.deleteNotification(notificationId, loginUserId);
        return ResponseEntity.noContent().build();

    }
}

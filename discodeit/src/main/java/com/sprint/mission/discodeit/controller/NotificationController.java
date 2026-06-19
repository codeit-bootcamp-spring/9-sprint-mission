package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  @Override
  @GetMapping
  public ResponseEntity<List<NotificationDto>> findAll(Authentication authentication) {
    UUID receiverId = getCurrentUserId(authentication);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(notificationService.findAllByReceiverId(receiverId));
  }

  @Override
  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID notificationId,
      Authentication authentication
  ) {
    UUID requesterId = getCurrentUserId(authentication);
    notificationService.delete(notificationId, requesterId);
    return ResponseEntity.noContent().build();
  }

  private UUID getCurrentUserId(Authentication authentication) {
    if (authentication == null
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      throw new DiscodeitException(ErrorCode.AUTHENTICATION_REQUIRED, Map.of());
    }
    return userDetails.getUserDto().id();
  }
}

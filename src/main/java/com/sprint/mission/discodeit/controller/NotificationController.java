package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  @Override
  public ResponseEntity<List<NotificationDto>> findAllByReceiverId(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UUID receiverId = userDetails.getUserDto().id();
    List<NotificationDto> dtos = notificationService.findAllByReceiverId(receiverId);
    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }

  @Override
  public ResponseEntity<Void> delete(@PathVariable("notificationId") UUID notificationId, @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UUID requesterId = userDetails.getUserDto().id();
    notificationService.delete(notificationId, requesterId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}


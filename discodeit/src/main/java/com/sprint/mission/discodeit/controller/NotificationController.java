package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @GetMapping
  @Override
  public ResponseEntity<List<NotificationDto>> getNotifications(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    UUID receiverId = extractUserId(userDetails);
    log.info("알림 조회 요청 수신: 사용자 ID={}", receiverId);

    List<NotificationDto> response = notificationService.findAllByReceiverId(receiverId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @DeleteMapping(path = "{notificationId}")
  @Override
  public ResponseEntity<Void> checkNotification(
      @PathVariable("notificationId") UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    UUID requesterId = userDetails.getUserDto().id();
    log.info("알림 확인(삭제) 요청 수신: 알림 ID={}, 요청자 ID={}", notificationId, requesterId);

    notificationService.checkAndDelete(notificationId, requesterId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  private UUID extractUserId(DiscodeitUserDetails userDetails) {
    if (userDetails != null && userDetails.getUserDto() != null) {
      return userDetails.getUserDto().id();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof User user) {
      return userRepository.findByUsername(user.getUsername())
          .map(com.sprint.mission.discodeit.entity.User::getId)
          .orElseThrow(() -> new IllegalStateException(
              "해당 인증 정보의 유저를 DB에서 찾을 수 없습니다: " + user.getUsername()));
    }

    throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
  }
}
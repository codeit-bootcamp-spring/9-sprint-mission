package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class S3UploadFailedEventListener {

  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Async
  @EventListener
  public void on(S3UploadFailedEvent event) {
    String title = "파일 업로드 최종 실패";
    String content = String.format("RequestId: %s, BinaryContentId: %s, Error: %s",
        event.requestId(), event.binaryContentId(), event.errorMessage());

    userRepository.findAllByRole(Role.ADMIN).forEach(admin -> {
      try {
        notificationService.create(admin.getId(), title, content);
        log.info("관리자 알림 전송: adminId={}", admin.getId());
      } catch (Exception e) {
        log.error("관리자 알림 전송 실패: adminId={}", admin.getId(), e);
      }
    });
  }
}

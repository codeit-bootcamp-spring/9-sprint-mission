package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.event.NotificationCacheEvictor;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class BinaryContentUploadFailureNotifier {

  private static final String REQUEST_ID = "requestId";
  private static final String TASK_NAME = "S3 binary content upload";

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final NotificationCacheEvictor cacheEvictor;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int notifyAdmins(UUID binaryContentId, Throwable cause) {
    return notifyAdmins(new S3UploadFailedEvent(
        TASK_NAME,
        MDC.get(REQUEST_ID),
        binaryContentId,
        cause.getMessage()
    ), null);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int notifyAdmins(S3UploadFailedEvent event) {
    return notifyAdmins(event, null);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int notifyAdmins(S3UploadFailedEvent event, String eventKey) {
    List<User> admins = userRepository.findAllByRole(UserRole.ADMIN);
    if (admins.isEmpty()) {
      return 0;
    }

    String content = """
        Task: %s
        RequestId: %s
        BinaryContentId: %s
        Error: %s
        """.formatted(
        event.taskName(),
        event.requestId(),
        event.binaryContentId(),
        event.errorMessage()
    ).trim();

    List<Notification> notifications = admins.stream()
        .filter(admin -> shouldCreate(admin.getId(), eventKey))
        .map(admin -> new Notification(
            admin,
            "바이너리 데이터 저장에 실패했습니다.",
            content,
            eventKey
        ))
        .toList();
    notificationRepository.saveAll(notifications);
    cacheEvictor.evictReceivers(notifications.stream()
        .map(Notification::getReceiver)
        .map(User::getId)
        .toList());
    return notifications.size();
  }

  private boolean shouldCreate(UUID receiverId, String eventKey) {
    return eventKey == null || !notificationRepository.existsByReceiverIdAndEventKey(
        receiverId,
        eventKey
    );
  }
}

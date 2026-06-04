package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
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

  @CacheEvict(cacheNames = CacheConfig.NOTIFICATIONS_BY_RECEIVER, allEntries = true)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void notifyAdmins(UUID binaryContentId, Throwable cause) {
    List<User> admins = userRepository.findAllByRole(UserRole.ADMIN);
    if (admins.isEmpty()) {
      return;
    }

    String content = """
        Task: %s
        RequestId: %s
        BinaryContentId: %s
        Error: %s
        """.formatted(
        TASK_NAME,
        MDC.get(REQUEST_ID),
        binaryContentId,
        cause.getMessage()
    ).trim();

    List<Notification> notifications = admins.stream()
        .map(admin -> new Notification(admin, "바이너리 데이터 저장에 실패했습니다.", content))
        .toList();
    notificationRepository.saveAll(notifications);
  }
}

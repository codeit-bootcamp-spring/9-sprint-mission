package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class NotificationNotFoundException extends DiscodeitException {

  public NotificationNotFoundException(Map<String, Object> details) {
    super(ErrorCode.NOTIFICATION_NOT_FOUND, details);
  }

  public static NotificationNotFoundException withId(UUID id) {
    return new NotificationNotFoundException(Map.of("notificationId", id));
  }
}

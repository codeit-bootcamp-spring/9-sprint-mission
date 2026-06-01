package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UnauthorizedNotificationException extends DiscodeitException {

  public UnauthorizedNotificationException(Map<String, Object> details) {
    super(ErrorCode.UNAUTHORIZED_NOTIFICATION, details);
  }

  public static UnauthorizedNotificationException withId(UUID id) {
    return new UnauthorizedNotificationException(Map.of("notificationId", id));
  }
}

package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class UnauthorizedNotificationException extends DiscodeitException {

  public UnauthorizedNotificationException() {
    super(ErrorCode.NOTIFICATION_FORBIDDEN);
  }

  public static UnauthorizedNotificationException withId(UUID id) {
    UnauthorizedNotificationException ex = new UnauthorizedNotificationException();
    ex.addDetail("notificationId", id);
    return ex;
  }
}

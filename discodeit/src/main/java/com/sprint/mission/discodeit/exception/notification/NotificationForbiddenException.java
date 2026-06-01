package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class NotificationForbiddenException extends NotificationException {

  public NotificationForbiddenException() {
    super(ErrorCode.NOTIFICATION_FORBIDDEN);
  }

  public static NotificationForbiddenException forNotification(UUID notificationId) {
    NotificationForbiddenException exception = new NotificationForbiddenException();
    exception.addDetail("notificationId", notificationId);
    return exception;
  }
}

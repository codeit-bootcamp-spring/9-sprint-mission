package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class NotificationAccessDenied extends NotificationException {

  public NotificationAccessDenied() {
    super(ErrorCode.NOTIFICATION_ACCESS_DENIED);
  }
}

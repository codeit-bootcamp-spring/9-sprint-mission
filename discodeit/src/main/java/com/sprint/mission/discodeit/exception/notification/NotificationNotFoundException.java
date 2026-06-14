package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.base.ErrorCode;
import com.sprint.mission.discodeit.exception.base.NotificationException;
import java.util.Collections;

public class NotificationNotFoundException extends NotificationException {

  public NotificationNotFoundException() {
    super(ErrorCode.NOTIFICATION_NOT_FOUNT, Collections.emptyMap());
  }
}

package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.base.ErrorCode;
import com.sprint.mission.discodeit.exception.base.NotificationException;
import java.util.Collections;

public class NotificationAccessDeniedException extends NotificationException {

  public NotificationAccessDeniedException() {

    super(ErrorCode.NOTIFICATION_ACCESS_DENIED, Collections.emptyMap());
  }
}

package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class AuthorizationDeniedException extends NotificationException {

  public AuthorizationDeniedException() {
    super(ErrorCode.AUTHORIZATION_DENIED);
  }

  public static AuthorizationDeniedException withId(UUID notificationId) {
    AuthorizationDeniedException exception = new AuthorizationDeniedException();
    exception.addDetail("notificationId", notificationId);
    return exception;
  }
}

package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class NotificationNotFoundException extends RuntimeException {

  public NotificationNotFoundException(String message) {
    super(message);
  }

  public static NotificationNotFoundException withId(UUID id) {
    return new NotificationNotFoundException("해당 알림을 찾을 수 없습니다. ID: " + id);
  }
}

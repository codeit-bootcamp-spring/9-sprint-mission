package com.sprint.mission.discodeit.exception.notification;

import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {
  private NotificationNotFoundException(UUID id) {
    super("Notification not found: " + id);
  }

  public static NotificationNotFoundException withId(UUID id) {
    return new NotificationNotFoundException(id);
  }
}


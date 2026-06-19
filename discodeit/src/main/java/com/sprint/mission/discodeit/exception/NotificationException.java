package com.sprint.mission.discodeit.exception;

public class NotificationException extends DiscodeitException {

  private final String notificationId;

  public NotificationException(ErrorCode errorCode) {
    super(errorCode);
    this.notificationId = null;
  }

  public NotificationException(ErrorCode errorCode, String notificationId) {
    super(errorCode);
    this.notificationId = notificationId;
  }

  public String getNotificationId() {
    return notificationId;
  }
}
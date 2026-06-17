package com.sprint.mission.discodeit.exception.notification;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotificationNotFoundException extends RuntimeException {

  public NotificationNotFoundException(String message) {
    super(message);
  }

  public static NotificationNotFoundException withId(UUID id) {
    return new NotificationNotFoundException("해당 알림을 찾을 수 없습니다. id: " + id);
  }
}

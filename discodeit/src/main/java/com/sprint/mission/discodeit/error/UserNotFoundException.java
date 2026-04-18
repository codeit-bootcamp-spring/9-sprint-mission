package com.sprint.mission.discodeit.error;

import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(Map<String, Object> details) {
    super(details.toString());
  }

  public UserNotFoundException(String message) {
    super(message);
  }

  public static UserNotFoundException withId(UUID userId) {
    return new UserNotFoundException("User not found with id: " + userId);
  }
}
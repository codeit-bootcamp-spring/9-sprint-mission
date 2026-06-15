package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @PastOrPresent(message = "Last read time must be past or present")
    Instant newLastReadAt,
    Boolean newNotificationEnabled
) {

  public ReadStatusUpdateRequest(Instant newLastReadAt) {
    this(newLastReadAt, null);
  }
}

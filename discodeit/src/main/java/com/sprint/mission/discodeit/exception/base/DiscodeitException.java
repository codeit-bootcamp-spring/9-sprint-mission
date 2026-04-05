package com.sprint.mission.discodeit.exception.base;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class DiscodeitException extends RuntimeException{
  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  protected DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = Collections.unmodifiableMap(details);
  }
}

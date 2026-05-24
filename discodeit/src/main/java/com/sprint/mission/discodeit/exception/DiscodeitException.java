package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException{
  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());

    this.timestamp = Instant.now();
    this.details = details;
    this.errorCode = errorCode;
  }

  public DiscodeitException(ErrorCode errorCode) {
    this(errorCode, new HashMap<>());
  }
}

package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import java.time.Instant;
import java.util.Map;

@Getter
public abstract class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  protected DiscodeitException(ErrorCode errorCode, Object... args) {
    super(String.format(errorCode.getMessage(), args));
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = Map.of("args", args);
  }
}
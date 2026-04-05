package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    this(Instant.now(), errorCode, details);
  }

  public DiscodeitException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(Objects.requireNonNull(errorCode, "errorCode must not be null").getMessage());
    this.timestamp = timestamp == null ? Instant.now() : timestamp;
    this.errorCode = errorCode;
    this.details = details == null ? Map.of() : Map.copyOf(details);
  }

}

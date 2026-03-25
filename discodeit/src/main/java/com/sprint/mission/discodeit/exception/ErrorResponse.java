package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status

) {

  public static ErrorResponse from(DiscodeitException ex) {
    return new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getCode(),
        ex.getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus().value()
    );
  }
}
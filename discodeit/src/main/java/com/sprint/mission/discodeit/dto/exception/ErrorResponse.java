package com.sprint.mission.discodeit.dto.exception;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
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
  public static ErrorResponse of(DiscodeitException ex, int status) {
    return new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().name(),
        ex.getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        status
    );
  }
}

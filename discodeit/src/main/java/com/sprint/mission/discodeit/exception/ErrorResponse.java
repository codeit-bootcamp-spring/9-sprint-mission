package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {

  public static ErrorResponse of(HttpStatus status, String message, HttpServletRequest req) {
    return new ErrorResponse(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        req.getRequestURI()
    );
  }
}

package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import java.time.Instant;
import java.util.Map;

@Getter
public class ErrorResponse {

  private final Instant timestamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;
  
  public ErrorResponse(Instant timestamp, String code, String message,
      Map<String, Object> details, String exceptionType, int status) {
    this.timestamp = timestamp;
    this.code = code;
    this.message = message;
    this.details = details;
    this.exceptionType = exceptionType;
    this.status = status;
  }
}
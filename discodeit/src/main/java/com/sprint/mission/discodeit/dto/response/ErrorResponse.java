package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {

  private final Instant timestamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;

  public ErrorResponse(String code, String message, Map<String, Object> details, String exceptionType, int status) {
    this.timestamp = Instant.now();
    this.code = code;
    this.message = message;
    this.details = details != null ? details : new HashMap<>();
    this.exceptionType = exceptionType;
    this.status = status;
  }


  public static ErrorResponse of(String code, String message, String exceptionType, int status) {
    return new ErrorResponse(code, message, null, exceptionType, status);
  }


  public static ErrorResponse of(String code, String message, Map<String, Object> details, String exceptionType, int status) {
    return new ErrorResponse(code, message, details, exceptionType, status);
  }


  public Instant getTimestamp() { return timestamp; }
  public String getCode() { return code; }
  public String getMessage() { return message; }
  public Map<String, Object> getDetails() { return details; }
  public String getExceptionType() { return exceptionType; }
  public int getStatus() { return status; }
}
package com.sprint.mission.discodeit.exception;

import java.time.Instant;

import java.util.HashMap;

import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

  private final Instant timeStamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;


  public static ErrorResponse of(Instant timeStamp, String code, String message,
      Map<String, Object> details,
      String exceptionType, int status) {
    return ErrorResponse.builder()
        .timeStamp(timeStamp != null ? timeStamp : Instant.now())
        .code(code)
        .message(message)
        .details(details != null ? details : new HashMap<>())
        .exceptionType(exceptionType)
        .status(status)
        .build();

  }

}

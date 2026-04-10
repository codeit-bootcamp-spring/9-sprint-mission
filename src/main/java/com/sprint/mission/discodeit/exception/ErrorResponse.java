package com.sprint.mission.discodeit.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

  private int status;
  private String exceptionType;
  private String errorCode;
  private String message;
  private Instant timestamp;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<String, Object> details;

  public static ErrorResponse from(DiscodeitException exception) {
    return ErrorResponse.builder()
        .status(exception.getErrorCode().getHttpStatus())
        .exceptionType(exception.getClass().getSimpleName())
        .errorCode(exception.getErrorCode().getCode())
        .message(exception.getErrorCode().getMessage())
        .timestamp(exception.getTimestamp())
        .details(exception.getDetails())
        .build();
  }
}


package com.sprint.mission.discodeit.exception;


import java.time.Instant;
import java.util.Map;
import lombok.Getter;


@Getter
public class DiscodeitException extends RuntimeException {

  protected final Instant timeStamp;
  protected final ErrorCode errorCode;
  protected final Map<String, Object> details;

  public DiscodeitException(ErrorCode errorCode) {
    this(errorCode, Map.of());
  }

  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());
    this.timeStamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details;
  }


}

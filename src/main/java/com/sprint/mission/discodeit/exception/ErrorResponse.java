package com.sprint.mission.discodeit.exception;

import java.time.Instant;

public record ErrorResponse(
    int code,
    String message,
    Instant timestamp
) {
  // timestamp 자동 주입 생성자
  public ErrorResponse(int code, String message) {
    this(code, message, Instant.now());
  }
}
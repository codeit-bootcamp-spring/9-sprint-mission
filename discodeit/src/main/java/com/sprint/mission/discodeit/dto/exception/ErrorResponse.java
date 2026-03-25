package com.sprint.mission.discodeit.dto.exception;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Objects> details,
    String exceptionType,
    int status
) {
}

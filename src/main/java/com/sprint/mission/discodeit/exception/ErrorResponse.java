package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

/**
 * API 에러 응답 DTO.
 * GlobalExceptionHandler에서 일관된 에러 응답 형식을 보장한다.
 */
public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

    /** DiscodeitException으로부터 ErrorResponse를 생성하는 팩토리 메서드 */
    public static ErrorResponse from(DiscodeitException ex) {
        return new ErrorResponse(
            ex.getTimestamp(),
            ex.getErrorCode().name(),
            ex.getMessage(),
            ex.getDetails(),
            ex.getClass().getSimpleName(),
            ex.getErrorCode().getStatus().value()
        );
    }
}
